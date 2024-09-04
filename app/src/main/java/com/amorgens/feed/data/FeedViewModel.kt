package com.amorgens.feed.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amorgens.feed.domain.CreatePostReq
import com.amorgens.feed.domain.FeedUIState
import com.amorgens.feed.domain.Post
import com.amorgens.general.data.APIService
import com.amorgens.general.data.GetUserToken
import com.amorgens.trade.domain.response.GenericResp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    private val _allPosts = MutableStateFlow(listOf(Post()))
    val allPost = _allPosts.asStateFlow()

    private val _feedUIState = MutableStateFlow(FeedUIState())
    val feedUIState= _feedUIState.asStateFlow()


    fun clearModelData(){
        _feedUIState.value = FeedUIState()
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
                Log.d("XXX TOKEN", token)
                try {
                    val resp = apiService.createPost(post, mapOf("Authorization" to token))
                    Log.d("CREATE POST ",resp.body().toString())
                    if (resp.isSuccessful){
                        val newPost = resp.body()?.data
                        if (newPost != null){
                            val  posts = _allPosts.value.toMutableList()
                            posts.add(newPost)
                            _allPosts.value = posts
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
                        Log.d("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isCreatePostLoading = false,
                        isCreatePostError = true,
                        isCreatePostSuccess = false,
                        createPostErrorMessage = e.toString()
                    ) }
                   Log.d("XX ERROR CREATING POST ", e.toString())
                }

            }
        }
    }


    fun getAllPosts(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                _feedUIState.update { it.copy(isFeedLoading = true) }
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
                            getFeedErrorMessage = ""
                        ) }
                    }else{
                        //Log.d("XX ERROR CREATING POST ", resp.errorBody()?.string() +"")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _feedUIState.update { it.copy(
                            isFeedLoading  = false,
                            isFeedLoadingError  = true,
                            isFeedLoadingSuccess  = false,
                            getFeedErrorMessage = errorResp.message
                        ) }
                        Log.d("XX ERROR CREATING POST ", errorResp.server_message+"")
                    }


                }catch (e:Exception){
                    _feedUIState.update { it.copy(
                        isFeedLoading  = false,
                        isFeedLoadingError  = true,
                        isFeedLoadingSuccess  = false,
                        getFeedErrorMessage = e.toString()
                    ) }
                    Log.d("XX ERROR CREATING POST ", e.toString())
                }

            }
        }
    }
}