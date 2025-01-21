package com.vhennus.feed.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhennus.feed.domain.CreatePostReq
import com.vhennus.feed.domain.FeedUIState
import com.vhennus.feed.domain.Post
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.domain.SystemData
import com.vhennus.general.utils.CLog
import com.vhennus.trade.domain.response.GenericResp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.feed.domain.Comment
import com.vhennus.feed.domain.CreateCommentReq
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class  FeedViewModel @Inject constructor(
    private val apiService: APIService,
    private val application: Application,
    private val getUserToken: GetUserToken
) : ViewModel() {

    private val _newPost = MutableStateFlow(Post())
    val newPost = _newPost.asStateFlow()

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPost = _allPosts.asStateFlow()

    private val _allMyPosts = MutableStateFlow<List<Post>>(emptyList())
    val allMyPost = _allMyPosts.asStateFlow()

    private val _feedUIState = MutableStateFlow(FeedUIState())
    val feedUIState= _feedUIState.asStateFlow()

    private val _systemData = MutableStateFlow(SystemData())
    val systemData = _systemData.asStateFlow()
    
    private val _singlePost = MutableStateFlow(Post())
    val singlePost = _singlePost.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()
    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    fun clearModelData(){
        _feedUIState.value = FeedUIState()
    }

    fun getUserName(){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
    }

    fun getSystemData(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try{

                    val resp = apiService.getSystemData()
                    if (resp.isSuccessful){
                        val systemData = resp.body()?.data
                        if (systemData == null){
                            CLog.error("ERROR GETTING SYSTEM DATA", " Did not get any data")
                            return@withContext
                        }
                        _systemData.value = systemData
                        _feedUIState.update { it.copy(isGetSystemDataSuccess = false) }

                    }else{
                        val errData = resp.errorBody()?.string()
                        CLog.error("ERROR GETTING SYSTEM DATA", resp.code().toString()+"err data")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(errData, genericType)
                        CLog.error("ERROR GETTING SYSTEM DATA", errorResp.message)

                    }
                }catch (e:Exception){
                    CLog.error("ERROR GETTING SYSTEM DATA", e.toString())
                }

            }
        }
    }

    fun success(){
        _feedUIState.update { it.copy(isCreatePostSuccess = true) }
    }
    fun createPost(post: CreatePostReq){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _feedUIState.update { it.copy(isCreatePostLoading = true) }
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
                            getAllPosts()

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


    fun getAllPosts(){
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
                        _feedUIState.update { it.copy(
                            isFeedLoading  = false,
                            isFeedLoadingError  = false,
                            isFeedLoadingSuccess  = true,
                            getFeedErrorMessage = "",
                            isScrollToFeedTop = true
                        ) }
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
                        CLog.error("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isFeedLoading  = false,
                        isFeedLoadingError  = true,
                        isFeedLoadingSuccess  = false,
                        getFeedErrorMessage = e.toString()
                    ) }
                    CLog.error("XX ERROR CREATING POST ", e.toString())
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
                        getSinglePostErrorMessage = "Unauthorized"
                    ) }
                }
                try {
                    val resp = apiService.getSinglePost(id, mapOf("Authorization" to token))
                    if (resp.isSuccessful){
                        val post = resp.body()?.data
                        if (post != null){
                            _singlePost.value = post
                        }
                        _feedUIState.update { it.copy(
                            isGetSinglePostLoading  = false,
                            isGetSinglePostError  = false,
                            isGetSinglePostSuccess  = true,
                            getSinglePostErrorMessage = ""
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
                            getSinglePostErrorMessage = errorResp.message
                        ) }
                        CLog.error("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }

                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isGetSinglePostLoading  = false,
                        isGetSinglePostError  = true,
                        isGetSinglePostSuccess  = false,
                        getSinglePostErrorMessage = e.toString()
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
                        CLog.debug("ALL MY POSTS ", allPosts.toString())
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
}