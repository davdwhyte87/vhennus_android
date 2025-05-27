package com.vhennus.feed.data

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.vhennus.feed.domain.CreatePostReq
import com.vhennus.feed.domain.FeedUIState
import com.vhennus.feed.domain.Post
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.domain.SystemData
import com.vhennus.general.utils.CLog
import com.vhennus.general.domain.GenericResp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.feed.domain.Comment
import com.vhennus.feed.domain.CreateCommentReq
import com.vhennus.feed.domain.LikedPost
import com.vhennus.feed.domain.LikedPostDao
import com.vhennus.feed.domain.PostFeed
import com.vhennus.feed.domain.PostWithComments
import com.vhennus.general.utils.ImageUploadWorker
import com.vhennus.profile.domain.UpdateProfileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class  FeedViewModel @Inject constructor(
    private val apiService: APIService,
    private val application: Application,
    private val getUserToken: GetUserToken,
    private val likedPostDao: LikedPostDao
) : ViewModel() {

    private val _newPost = MutableStateFlow(Post())
    val newPost = _newPost.asStateFlow()

    private val _allPosts = MutableStateFlow<List<PostFeed>>(emptyList())
    val allPost = _allPosts.asStateFlow()

    private val _allMyPosts = MutableStateFlow<List<PostFeed>>(emptyList())
    val allMyPost = _allMyPosts.asStateFlow()

    private val _allOtherUserPost = MutableStateFlow<List<PostFeed>>(emptyList())
    val allOtherUserPost = _allOtherUserPost.asStateFlow()


    private val _feedUIState = MutableStateFlow(FeedUIState())
    val feedUIState= _feedUIState.asStateFlow()


    
    private val _singlePost = MutableStateFlow(PostWithComments())
    val singlePost = _singlePost.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()
    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _imageURI = MutableStateFlow<Uri?>(null)
    val imageUri = _imageURI.asStateFlow()

    private val _likedPosts = MutableStateFlow<List<String>>(emptyList())
    val likedPosts = _likedPosts.asStateFlow()


    fun setImageURI(uri: Uri){
        _imageURI.value = uri
    }
    fun clearModelData(){
        _imageURI.value = null
    }

    fun clearUIData(){
        _feedUIState.value = FeedUIState()
    }

    fun getUserName(){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
    }



    fun success(){
        _feedUIState.update { it.copy(isCreatePostSuccess = true) }
    }

    fun createPostB(post: CreatePostReq){
        _feedUIState.update { it.copy(isCreatePostLoading = true) }
        // check if there is an image in post, if so upload it
        val tempUri = _imageURI.value
        if(tempUri !=null){

            uploadImage(tempUri)
            // now wait for upload to finish

        }else{
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    createPost(post)
                }
            }

        }
    }

    fun createPost(post: CreatePostReq){
        _feedUIState.update { it.copy(isCreatePostLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){

                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isCreatePostLoading = false,
                        isCreatePostError = true,
                        isCreatePostSuccess = false,
                        createPostErrorMessage = "Unauthorized"
                    ) }
                    return@withContext
                }
                CLog.error("XXX TOKEN", token)
                try {
                    val resp = apiService.createPost(post, mapOf("Authorization" to token))
                    CLog.error("CREATE POST ",resp.body().toString())
                    if (resp.isSuccessful){
                        val newPost = resp.body()?.data
                        if (newPost != null){
                            val  posts = _allPosts.value.toMutableList()
                            //posts.add(newPost)
//                            _allPosts.value = posts
                            getAllPosts(true)

                        }
                        _feedUIState.update { it.copy(
                            isCreatePostLoading = false,
                            isCreatePostError = false,
                            isCreatePostSuccess = true,
                            createPostErrorMessage = ""
                        ) }
                    }else{
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isCreatePostLoading = false,
                            isCreatePostError = true,
                            isCreatePostSuccess = false,
                            createPostErrorMessage = errorResp.message
                        ) }
                        CLog.error("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isCreatePostLoading = false,
                        isCreatePostError = true,
                        isCreatePostSuccess = false,
                        createPostErrorMessage = e.toString()
                    ) }
                   CLog.error("XX ERROR CREATING POST ", e.toString())
                }

            }
        }
    }




    fun getAllPosts(isScrollToTop:Boolean){
        _feedUIState.update { it.copy(isFeedLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isFeedLoading  = false,
                        isFeedLoadingError  = true,
                        isFeedLoadingSuccess  = false,
                         getFeedErrorMessage = "Unauthorized"
                    ) }
                }
                try {
                    val resp = apiService.getAllPosts(mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val allPosts = resp.body()?.data
                        if (allPosts != null){
                            _allPosts.value = allPosts
                        }
                       // CLog.debug("GET ALL POSTS REQ", resp.body().toString())
                        //CLog.debug("GET ALL POSTS", allPosts.toString())
                        _feedUIState.update { it.copy(
                            isFeedLoading  = false,
                            isFeedLoadingError  = false,
                            isFeedLoadingSuccess  = true,
                            getFeedErrorMessage = "",

                        ) }

                        // scroll screen to top if asked to do so
                        if(isScrollToTop){
                            _feedUIState.update { it.copy(isScrollToFeedTop = true) }
                        }
                    }else{
                        //CLog.error("XX ERROR CREATING POST ", resp.errorBody()?.string() +"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isFeedLoading  = false,
                            isFeedLoadingError  = true,
                            isFeedLoadingSuccess  = false,
                            getFeedErrorMessage = errorResp.message
                        ) }
                        CLog.error("XX ERROR GETTING POST ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isFeedLoading  = false,
                        isFeedLoadingError  = true,
                        isFeedLoadingSuccess  = false,
                        getFeedErrorMessage = e.toString()
                    ) }
                    CLog.error("XX ERROR GETTING POST ", e.toString())
                }

            }
        }
    }

    fun getSinglePosts(id:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _feedUIState.update { it.copy(isGetSinglePostLoading = true) }
                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isGetSinglePostLoading  = false,
                        isGetSinglePostError  = true,
                        isGetSinglePostSuccess  = false,
                        getSinglePostErrorMessage = "Unauthorized",
                        isGetSinglePostRefresh = false
                    ) }
                }
                try {
                    val resp = apiService.getSinglePost(id, mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val post = resp.body()?.data
                        if (post != null){
                            _singlePost.update { post }
                        }
                        _feedUIState.update { it.copy(
                            isGetSinglePostLoading  = false,
                            isGetSinglePostError  = false,
                            isGetSinglePostSuccess  = true,
                            getSinglePostErrorMessage = "",
                            isGetSinglePostRefresh = false
                        ) }
                    }else{
                        //CLog.error("XX ERROR CREATING POST ", resp.errorBody()?.string() +"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isGetSinglePostLoading  = false,
                            isGetSinglePostError  = true,
                            isGetSinglePostSuccess  = false,
                            getSinglePostErrorMessage = errorResp.message,
                            isGetSinglePostRefresh = false
                        ) }
                        CLog.error("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }

                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isGetSinglePostLoading  = false,
                        isGetSinglePostError  = true,
                        isGetSinglePostSuccess  = false,
                        getSinglePostErrorMessage = e.toString(),
                        isGetSinglePostRefresh = false
                    ) }
                    CLog.error("XX ERROR CREATING POST ", e.toString())
                }

            }
        }
    }

    fun createComment(id:String, newcomment: CreateCommentReq){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _feedUIState.update { it.copy(isCreateCommentButtonLoading = true) }
                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isCreateCommentError = true,
                        isCreateCommentSuccess = false,
                        isCreateCommentButtonLoading = false,
                        createCommentErrorMessage = "Unauthorized"
                    ) }
                    return@withContext
                }
                CLog.error("XXX TOKEN", token)
                try {
                    val resp = apiService.createComment(id,newcomment, mapOf("Authorization" to token))
                    //CLog.error("CREATE POST ",resp.body().toString())
                    if (resp.isSuccessful){
                        val comment = resp.body()?.data
                        if (comment != null){
                            val  comments = _comments.value.toMutableList()
                            comments.add(comment)
                            _comments.value = comments
                        }

                        // get the single post data
                        getSinglePosts(id)
                        _feedUIState.update { it.copy(
                            isCreateCommentError = false,
                            isCreateCommentSuccess = true,
                            isCreateCommentButtonLoading = false,
                            createCommentErrorMessage = ""
                        ) }
                    }else{
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isCreateCommentError = true,
                            isCreateCommentSuccess = false,
                            isCreateCommentButtonLoading = false,
                            createCommentErrorMessage = errorResp.message
                        ) }
                        CLog.error("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isCreateCommentError = true,
                        isCreateCommentSuccess = false,
                        isCreateCommentButtonLoading = false,
                        createCommentErrorMessage = e.toString()
                    ) }
                    CLog.error("XX ERROR CREATING POST ", e.toString())
                }

            }
        }
    }

    fun updateSinglePOstRefresh(data:Boolean){
        _feedUIState.update { it.copy(isGetSinglePostRefresh = data) }
    }

    fun updateFeedScrollToTop(data:Boolean){
        _feedUIState.update { it.copy(isScrollToFeedTop = data) }
    }

    fun likePost(id:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
//                _feedUIState.update { it.copy(isGetSinglePostLoading = true) }
                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isLikePostSuccess = false,
                        isLikePostError = true,
                        likePostErrorMessage = "Unauthorized"

                    ) }
                }
                try {
                    val resp = apiService.likePost(id, mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val post = resp.body()?.data

                        _feedUIState.update { it.copy(
                            isLikePostSuccess = true,
                            isLikePostError = false,
                            likePostErrorMessage = ""
                        ) }

                        // remove or add liked post to local database to keep track
//                        if(likedPosts.value.contains(id)){
//                            removeLikeLocal(id)
//                        }else{
//                            likePostLocal(id)
//                        }
                    }else{
                        //CLog.error("XX ERROR CREATING POST ", resp.errorBody()?.string() +"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isLikePostSuccess = false,
                            isLikePostError = true,
                            likePostErrorMessage = errorResp.message
                        ) }
                        CLog.error("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }

                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isLikePostSuccess = false,
                        isLikePostError = true,
                        likePostErrorMessage = e.toString()
                    ) }
                    CLog.error("XX ERROR CREATING POST ", e.toString())
                }

            }
        }
    }

    fun getAllMyPosts(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _feedUIState.update { it.copy(isGetAllMyPostsLoading = true) }
                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isGetAllMyPostsLoading  = false,
                        isGetAllMyPostsError  = true,
                        isGetAllMyPostsSuccess  = false,
                        getFeedErrorMessage = "Unauthorized"
                    ) }
                }
                try {
                    val resp = apiService.getAllMyPosts(mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val allPosts = resp.body()?.data
                        if (allPosts != null){
                            _allMyPosts.value = allPosts
                        }
                        //CLog.debug("ALL MY POSTS ", allPosts.toString())
                        _feedUIState.update { it.copy(
                            isGetAllMyPostsLoading  = false,
                            isGetAllMyPostsError  = false,
                            isGetAllMyPostsSuccess  = true,
                            getFeedErrorMessage = ""
                        ) }
                    }else{
                        //CLog.error("XX ERROR CREATING POST ", resp.errorBody()?.string() +"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isGetAllMyPostsLoading  = false,
                            isGetAllMyPostsError  = true,
                            isGetAllMyPostsSuccess  = false,
                            getFeedErrorMessage = errorResp.message
                        ) }
                        CLog.error("ALL MY POSTS ERROR ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isGetAllMyPostsLoading  = false,
                        isGetAllMyPostsError  = true,
                        isGetAllMyPostsSuccess  = false,
                        getFeedErrorMessage = e.toString()
                    ) }
                    CLog.error("ALL MY POSTS ERROR", e.toString())
                }

            }
        }
    }


    fun getAllUserPosts(userName:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _feedUIState.update { it.copy(isGetAllMyPostsLoading = true) }
                val token = getUserToken.getUserToken()
                if (token.isBlank()){
                    _feedUIState.update { it.copy(
                        isGetAllOtherUserPostsLoading  = false,
                        isGetAllOtherUserPostsError  = true,
                        isGetAllOtherUserPostsSuccess  = false,
                        getAllOtherUserPostsErrorMessage = "Unauthorized"
                    ) }
                }

                try {
                    val resp = apiService.getAllUserPosts(userName,mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val allPosts = resp.body()?.data
                        if (allPosts != null){
                            _allOtherUserPost.value = allPosts
                        }
                        //CLog.debug("ALL MY POSTS ", allPosts.toString())
                        _feedUIState.update { it.copy(
                            isGetAllOtherUserPostsLoading  = false,
                            isGetAllOtherUserPostsError  = false,
                            isGetAllOtherUserPostsSuccess  = true,
                            getAllOtherUserPostsErrorMessage = ""
                        ) }
                    }else{
                        //CLog.error("XX ERROR CREATING POST ", resp.errorBody()?.string() +"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isGetAllOtherUserPostsLoading  = false,
                            isGetAllOtherUserPostsError  = true,
                            isGetAllOtherUserPostsSuccess  = false,
                            getAllOtherUserPostsErrorMessage = errorResp.message
                        ) }
                        CLog.error("ALL OTHER USER POSTS ERROR ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isGetAllOtherUserPostsLoading  = false,
                        isGetAllOtherUserPostsError  = true,
                        isGetAllOtherUserPostsSuccess  = false,
                        getAllOtherUserPostsErrorMessage = e.toString()
                    ) }
                    CLog.error("ALL OTHER USER POSTS ERROR", e.toString())
                }

            }
        }
    }

    fun getLikedPost(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               _likedPosts.value = likedPostDao.getAllLikedPosts()
            }
        }
    }

    fun likePostLocal(id: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                likedPostDao.insertLikedPost(LikedPost(postId = id))
            }
            getLikedPost()
        }
    }

    fun removeLikeLocal(id:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                likedPostDao.removeLikedPost(LikedPost(postId = id))
            }
            getLikedPost()
        }
    }


    private val _workStatus = MutableStateFlow<WorkInfo?>(null)
    val workStatus: StateFlow<WorkInfo?> = _workStatus.asStateFlow()
    private val workManager: WorkManager = WorkManager.getInstance(application)
    private var hasHandledUploadSuccess = false


    fun uploadImage(imageUri: Uri) {

        val inputData = workDataOf("imageUri" to imageUri.toString(),
            "publicID" to UUID.randomUUID().toString()
        )

        // Create the work request
        val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setInputData(inputData)
            .build()

        // Enqueue the upload work
        workManager.enqueue(uploadWorkRequest)

        // Observe the work status
        workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).observeForever { workInfo ->
            // Emit updates to the state flow
            if(workInfo != null && !hasHandledUploadSuccess){
                _workStatus.value = workInfo

                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    hasHandledUploadSuccess = true
                    workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).removeObserver { this }
                }

                // remove the oberserver when done
                if (workInfo?.state?.isFinished == true) {
                    workManager.getWorkInfoByIdLiveData(uploadWorkRequest.id).removeObserver { this }
                }
            }

        }
    }

    fun resetUploadWorkStatus() {
        _workStatus.value = null
        hasHandledUploadSuccess = false
    }

}