package com.vhennus.profile.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.Application
import com.vhennus.chat.domain.Chat
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.ImageUploadWorker
import com.vhennus.profile.domain.FriendRequest
import com.vhennus.profile.domain.Profile
import com.vhennus.profile.domain.ProfileUIState
import com.vhennus.profile.domain.SendFriendRequest
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.profile.presentation.FriendRequestItem
import com.vhennus.search.domain.SearchUIState
import com.vhennus.settings.domain.SettingsUIState
import com.vhennus.general.domain.GenericResp
import com.vhennus.profile.domain.FriendRequestWithProfile
import com.vhennus.profile.domain.MiniProfile
import com.vhennus.profile.domain.ProfileWithFriends
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserToken: GetUserToken,
    private val apiService: APIService,
    private val application: android.app.Application
):ViewModel() {

    private val _profile = MutableStateFlow(Profile())
    val profile = _profile.asStateFlow()

    private val _otherUserProfile = MutableStateFlow(ProfileWithFriends())
    val otherUserProfile = _otherUserProfile.asStateFlow()


    private val _myProfile = MutableStateFlow(ProfileWithFriends())
    val myProfile = _myProfile.asStateFlow()


    private val _profileUIState = MutableStateFlow(ProfileUIState())
    val profileUIState = _profileUIState.asStateFlow()

    private val _myFriendRequests = MutableStateFlow<List<FriendRequestWithProfile>>(emptyList())
    val myFriendRequests = _myFriendRequests.asStateFlow()

    private val _searchUIState = MutableStateFlow(SearchUIState())
    val searchUIState = _searchUIState.asStateFlow()

    private val _profileSearchResults = MutableStateFlow<List<MiniProfile>>(emptyList())
    val profileSearchResults = _profileSearchResults.asStateFlow()

    private val _settingsUIState = MutableStateFlow(SettingsUIState())
    val settingsUIState = _settingsUIState.asStateFlow()


    private val _workStatus = MutableStateFlow<WorkInfo?>(null)
    val workStatus: StateFlow<WorkInfo?> = _workStatus.asStateFlow()
    private val workManager: WorkManager = WorkManager.getInstance(application)
    private var hasHandledUploadSuccess = false


    fun uploadImage(imageUri: Uri) {
        val inputData = workDataOf("imageUri" to imageUri.toString(),
            "publicID" to _myProfile.value.profile.user_name)

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

    fun resetUIState(){
        _profileUIState.value = ProfileUIState()
        _searchUIState.value = SearchUIState()
        _settingsUIState.value = SettingsUIState()
    }
    fun resetModelData(){
        _profile.value = Profile()
        _myFriendRequests.value = emptyList()
        _profileSearchResults.value = emptyList()
    }



    fun getUserProfile(userName:String){
        _profileUIState.update { it.copy(
            isGetUserProfileLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.getUserProfile(userName, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _profileUIState.update { it.copy(
                                isGetUserProfileLoading = false,
                                isGetUserProfileSuccess = true,
                                isGetUserProfileError = false,
                                getUserProfileErrorMessage = ""
                            ) }
                            _otherUserProfile.value = data

                            CLog.debug("GET Other PROFILE RESPONSE", data.toString())
                        }else{
                            _profileUIState.update { it.copy(
                                isGetUserProfileLoading = false,
                                isGetUserProfileSuccess = false,
                                isGetUserProfileError = true,
                                getUserProfileErrorMessage = "Profile not available"
                            ) }

                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET other PROFILE RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isGetUserProfileLoading = false,
                            isGetUserProfileSuccess = false,
                            isGetUserProfileError = true,
                            getUserProfileErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isGetUserProfileLoading = false,
                        isGetUserProfileSuccess = false,
                        isGetUserProfileError = true,
                        getUserProfileErrorMessage = "Network Error"
                    ) }
                    CLog.error("GET other PROFILE RESPONSE", e.toString() +" ")
                }
            }
        }
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
                            _myProfile.value = data

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

    fun getNotificationToken(): String{
        val mshared = application.getSharedPreferences("firebase", Context.MODE_PRIVATE)
        val data = mshared.getString("token", "")
        return data.toString()
    }

    fun updateNotificationToken(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val notifyToken = getNotificationToken()

                    val data = UpdateProfileRequest(
                        null, null, null, notifyToken
                    )
                    CLog.debug("STORED FCM TOKEN", notifyToken)
                    if(notifyToken == ""){
                        return@withContext
                    }
                    val token = getUserToken.getUserToken()
                    val resp = apiService.updateProfile(data, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        CLog.debug("UPDATED TOKEN", "")
                        val data = resp.body()?.data
                        if(data !=null){

                        }else{

                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("UPDATE FIREBASE TOKEN ERROR", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                    }

                }catch (e:Exception){

                    CLog.error("UPDATE FIREBASE TOKEN ERROR", e.toString() +" ")
                }
            }
        }
    }

    fun updateProfile(data:UpdateProfileRequest){
        if(data.image!=null){
            _profileUIState.update { it.copy(
                isUploadImageLoading = true
            ) }
            CLog.debug("UPLOAD IMAGE LOADING", _profileUIState.value.isUploadImageLoading.toString())
        }

        if(data.bio != null || data.name!= null){
            _profileUIState.update { it.copy(
                isUpdateProfileLoading = true
            ) }
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.updateProfile(data, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        _profileUIState.update { it.copy(
                            isUpdateProfileLoading = false,
                            isUpdateProfileSuccess = true,
                            isUpdateProfileError = false,
                            updateProfileErrorMessage = "",
                            isUploadImageLoading = false
                        ) }
                        getMyProfile()

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("UPDATE PROFILE RESPONSE", respString +" ")
                        val json =  Json { coerceInputValues = true }
                        val errorResp = json.decodeFromString<GenericResp<String>>(respString.toString())
                        _profileUIState.update { it.copy(
                            isUpdateProfileLoading = false,
                            isUpdateProfileSuccess = false,
                            isUpdateProfileError = true,
                            updateProfileErrorMessage = errorResp.message,
                            isUploadImageLoading = false
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isUpdateProfileLoading = false,
                        isUpdateProfileSuccess = false,
                        isUpdateProfileError = true,
                        updateProfileErrorMessage = "Network Error",
                        isUploadImageLoading = false
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
                            _myFriendRequests.value = data.distinctBy { it.id }

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

    fun searchProfiles(data:String){
       _searchUIState.update { it.copy(
           isSearchLoading = true
       ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.searchProfile(data, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _searchUIState.update { it.copy(
                                isSearchLoading = false,
                                isSearchSuccess = true,
                                isSearchError = false,
                                searchErrorMessage = ""
                            ) }
                            _profileSearchResults.value = data
                            CLog.debug("SEARCH RESP ", data.toString())
                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("SEARCH ERROR", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _searchUIState.update { it.copy(
                            isSearchLoading = false,
                            isSearchSuccess = false,
                            isSearchError = true,
                            searchErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _searchUIState.update { it.copy(
                        isSearchLoading = false,
                        isSearchSuccess = false,
                        isSearchError = true,
                        searchErrorMessage = "Network"
                    ) }
                    CLog.error("SEARCH ERROR", e.toString() +" ")
                }
            }
        }
    }

    fun sendFriendRequest(data:SendFriendRequest){
        _profileUIState.update { it.copy(
            isSendFriendRequestLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.sendFriendRequest(data, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        CLog.debug("SEND FR", resp.body().toString())

                        _profileUIState.update { it.copy(
                            isSendFriendRequestLoading = false,
                            isSendFriendRequestSuccess = true,
                            isSendFriendRequestError = false,
                            sendFriendRequestError = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("SEND FRIEND REQUEST", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _profileUIState.update { it.copy(
                            isSendFriendRequestLoading = false,
                            isSendFriendRequestSuccess = false,
                            isSendFriendRequestError = true,
                            sendFriendRequestError = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _profileUIState.update { it.copy(
                        isSendFriendRequestLoading = false,
                        isSendFriendRequestSuccess = false,
                        isSendFriendRequestError = true,
                        sendFriendRequestError = "Network Error"
                    ) }
                    CLog.error("SEND FRIEND REQUEST ", e.toString() +" ")
                }
            }
        }
    }

    fun deleteAccount(){
       _settingsUIState.update { it.copy(isDeleteAccountLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.deleteAccount( mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        _settingsUIState.update { it.copy(
                            isDeleteAccountLoading = false,
                            isDeleteAccountError = false,
                            isDeleteAccountSuccess = true,
                            deleteAccountErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("DELETE ACCOUNT ERROR", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _settingsUIState.update { it.copy(
                            isDeleteAccountLoading = false,
                            isDeleteAccountError = true,
                            isDeleteAccountSuccess = false,
                            deleteAccountErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _settingsUIState.update { it.copy(
                        isDeleteAccountLoading = false,
                        isDeleteAccountError = true,
                        isDeleteAccountSuccess = false,
                        deleteAccountErrorMessage = "Network error"
                    ) }
                    CLog.error("DELETE ACCOUNT ERROR", e.toString() +" ")
                }
            }
        }
    }

}