package com.vhennus.profile.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.utils.CLog
import com.vhennus.profile.domain.FriendRequest
import com.vhennus.profile.domain.Profile
import com.vhennus.profile.domain.ProfileUIState
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.profile.presentation.FriendRequestItem
import com.vhennus.trade.domain.response.GenericResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserToken: GetUserToken,
    private val apiService: APIService
):ViewModel() {

    private val _profile = MutableStateFlow(Profile())
    val profile = _profile.asStateFlow()

    private val _profileUIState = MutableStateFlow(ProfileUIState())
    val profileUIState = _profileUIState.asStateFlow()

    private val _myFriendRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val myFriendRequests = _myFriendRequests.asStateFlow()

    fun resetUIState(){
        _profileUIState.value = ProfileUIState()
    }

    fun getMyProfile(){
        _profileUIState.update { it.copy(
            isGetProfileLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.getMyProfile(mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _profileUIState.update { it.copy(
                                isGetProfileLoading = false,
                                isGetProfileSuccess = true,
                                isGetProfileError = false,
                                isGetProfileErrorMessage = ""
                            ) }
                            _profile.value = data

                            CLog.debug("GET PROFILE RESPONSE", data.toString())
                        }else{
                            _profileUIState.update { it.copy(
                                isGetProfileLoading = false,
                                isGetProfileSuccess = false,
                                isGetProfileError = true,
                                isGetProfileErrorMessage = "No profile available"
                            ) }

                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET PROFILE RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isGetProfileLoading = false,
                            isGetProfileSuccess = false,
                            isGetProfileError = true,
                            isGetProfileErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isGetProfileLoading = false,
                        isGetProfileSuccess = false,
                        isGetProfileError = true,
                        isGetProfileErrorMessage = "Network Error"
                    ) }
                    CLog.error("GET PROFILE RESPONSE", e.toString() +" ")
                }
            }
        }
    }

    fun updateProfile(data:UpdateProfileRequest){
        _profileUIState.update { it.copy(
            isUpdateProfileLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.updateProfile(data, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _profileUIState.update { it.copy(
                                isUpdateProfileLoading = false,
                                isUpdateProfileSuccess = true,
                                isUpdateProfileError = false,
                                updateProfileErrorMessage = ""
                            ) }
                            _profile.value = data
                        }else{
//                            _profileUIState.update { it.copy(
//                                isGetProfileLoading = false,
//                                isGetProfileSuccess = false,
//                                isGetProfileError = true,
//                                isGetProfileErrorMessage = "No profile available"
//                            ) }

                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("UPDATE PROFILE RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isUpdateProfileLoading = false,
                            isUpdateProfileSuccess = false,
                            isUpdateProfileError = true,
                            updateProfileErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isUpdateProfileLoading = false,
                        isUpdateProfileSuccess = false,
                        isUpdateProfileError = true,
                        updateProfileErrorMessage = "Network Error"
                    ) }
                    CLog.error("UPDATE PROFILE RESPONSE", e.toString() +" ")
                }
            }
        }
    }

    fun getMyFriendRequests(){
        _profileUIState.update { it.copy(
            isGetFriendRequestsLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.getMyFriendRequests(mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _profileUIState.update { it.copy(
                                isGetFriendRequestsLoading = false,
                                isGetFriendRequestsSuccess = true,
                                isGetFriendRequestsError = false,
                                getFrienRequestsErrorMessage = ""
                            ) }
                            _myFriendRequests.value = data

                            CLog.debug("GET FRIEND REUQEST RESPONSE", data.toString())
                        }else{
                            _profileUIState.update { it.copy(
                                isGetFriendRequestsLoading = false,
                                isGetFriendRequestsSuccess = true,
                                isGetFriendRequestsError = false,
                                getFrienRequestsErrorMessage = ""
                            ) }

                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET FR RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isGetFriendRequestsLoading = false,
                            isGetFriendRequestsSuccess = false,
                            isGetFriendRequestsError = true,
                            getFrienRequestsErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isGetFriendRequestsLoading = false,
                        isGetFriendRequestsSuccess = false,
                        isGetFriendRequestsError = true,
                        getFrienRequestsErrorMessage = "Network Error"
                    ) }
                    CLog.error("GET FR RESPONSE", e.toString() +" ")
                }
            }
        }
    }

    fun acceptFriendRequest(id:String){
        _profileUIState.update { it.copy(
            isAcceptRequestLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.acceptFriendRequest(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        CLog.debug("ACCEPT FR", resp.body().toString())
                        val tempRequests = _myFriendRequests.value.filter { it.id != id }
                        _myFriendRequests.value = tempRequests

                        _profileUIState.update { it.copy(
                            isAcceptRequestLoading = false,
                            isAcceptRequestSuccess = true,
                            isAcceptRequestError = false,
                            acceptRequestErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("ACCEPT REQUEST RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isAcceptRequestLoading = false,
                            isAcceptRequestSuccess = false,
                            isAcceptRequestError = true,
                            acceptRequestErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isAcceptRequestLoading = false,
                        isAcceptRequestSuccess = false,
                        isAcceptRequestError = true,
                        acceptRequestErrorMessage = "Network Error"
                    ) }
                    CLog.error("ACCEPT REQUEST RESPONSE", e.toString() +" ")
                }
            }
        }
    }

    fun rejectFriendRequest(id:String){
        _profileUIState.update { it.copy(
            isRejectRequestLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.rejectFriendRequest(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data

                        val tempRequests = _myFriendRequests.value.filter { it.id != id }
                        _myFriendRequests.value = tempRequests

                        _profileUIState.update { it.copy(
                            isRejectRequestLoading = false,
                            isRejectRequestSuccess = true,
                            isRejectRequestError = false,
                            rejectRequestErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("REJECT REQUEST RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isRejectRequestLoading = false,
                            isRejectRequestSuccess = false,
                            isRejectRequestError = true,
                            rejectRequestErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isRejectRequestLoading = false,
                        isRejectRequestSuccess = false,
                        isRejectRequestError = true,
                        rejectRequestErrorMessage = "Network Error"
                    ) }
                    CLog.error("REJECT REQUEST RESPONSE", e.toString() +" ")
                }
            }
        }
    }

}