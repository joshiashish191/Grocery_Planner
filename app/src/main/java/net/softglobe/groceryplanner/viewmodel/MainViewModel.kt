package net.softglobe.groceryplanner.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.GroceryDao
import net.softglobe.groceryplanner.model.GroceryDatabase
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.Repository
import net.softglobe.groceryplanner.model.network.Result
import net.softglobe.groceryplanner.model.network.RetrofitInstance
import net.softglobe.groceryplanner.model.network.User
import net.softglobe.groceryplanner.model.network.request.BackUpRequest
import net.softglobe.groceryplanner.model.network.response.GrantRewardResponse
import net.softglobe.groceryplanner.model.network.response.LoginResponse
import net.softglobe.groceryplanner.model.network.response.MetaDataResponse
import net.softglobe.groceryplanner.model.network.response.PaymentFetchResponse
import net.softglobe.groceryplanner.model.network.response.UserMetaDataResponse
import retrofit2.Response

class MainViewModel(context: Context) : ViewModel() {
    private var groceryDao : GroceryDao
    private var repository : Repository
    private var preferences : Preferences

    private var _groceryList = MutableStateFlow<List<Grocery>>(emptyList())
    var groceryList = _groceryList.asStateFlow()

    private var _modificationsList = MutableStateFlow<List<Modification>>(emptyList())
    var modificationsList = _modificationsList.asStateFlow()

    init {
        groceryDao = GroceryDatabase.getInstance(context).groceryDao
        repository = Repository(groceryDao)
        preferences = Preferences(context)
        getGroceryList()
    }

    private fun getGroceryList() {
        viewModelScope.launch {
            repository.getGroceryList()
                .collect{ groceryList ->
                    _groceryList.value = groceryList
                }
        }
    }

    suspend fun getGroceryListWithoutObserver() : List<Grocery> {
        return repository.getGroceryListWithoutObserver()
    }

    fun getModificationsList(id : Int) {
        viewModelScope.launch {
            repository.getModificationsList(id).collect { modificationsList ->
                _modificationsList.value = modificationsList
            }
        }
    }

    fun insertGroceryItem(grocery: Grocery, modification: Modification) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.insertGroceryItem(grocery)
            modification.itemId = id.toInt()
            val modificationCount = repository.getModificationsCountByGroceryItemId(id.toInt())
            if (preferences.isPaidUser()) {
                repository.insertModification(modification)
            } else if (!preferences.isPaidUser() && modificationCount < Integer.parseInt(preferences.getModificationRecordsLimitForFree())) {
                repository.insertModification(modification)
            }
        }
    }

    suspend fun insertGroceryItemOnly(grocery: Grocery) : Long {
        return repository.insertGroceryItem(grocery)
    }

    suspend fun insertModification(modification: Modification) {
        repository.insertModification(modification)
    }

    fun deleteGroceryItem(id : Int) {
        viewModelScope.launch {
            repository.deleteGroceryItem(id)
            repository.deleteAllModificationsByGroceryItemId(id)
        }
    }

    fun deleteSingleModificationEntry(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSingleModificationEntry(id)
        }
    }

    fun deleteAllModificationsByGroceryItemId(id : Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllModificationsByGroceryItemId(id)
        }
    }

    suspend fun getGroceryItem(id : Int) : Grocery = repository.getGroceryItemById(id)

    fun searchGroceryListByQuery(query : String) : LiveData<List<Grocery>> {
        return repository.searchGroceryListByQuery(query)
    }

    fun getSortedGroceryListByName() : LiveData<List<Grocery>> {
        return repository.getSortedGroceryListByName()
    }

    fun getSortedGroceryListByDateAdded() : LiveData<List<Grocery>> {
        return repository.getSortedGroceryListByDateAdded()
    }

    fun getLowStockGroceryItems() : LiveData<List<Grocery>> {
        return repository.getLowStockGroceryItems()
    }

    suspend fun getGroceryItemsCount() : Int {
        return repository.getGroceryItemsCount()
    }

    suspend fun loginUser(email : String, password : String) : Response<LoginResponse> {
        return RetrofitInstance.api.loginUser(email, password)
    }

    suspend fun registerUser(user: User) : Response<Result> {
        return RetrofitInstance.api.registerUser(user)
    }

    suspend fun forgotPassword(email: String, code : String) : Response<Result> {
        return RetrofitInstance.api.forgotPassword(email, code)
    }

    suspend fun resetPassword(email: String, newPassword : String) : Response<Result> {
        return RetrofitInstance.api.resetPassword(email, newPassword)
    }

    suspend fun changePassword(email: String, oldPassword : String, newPassword : String, authToken : String) : Response<Result> {
        return RetrofitInstance.api.changePassword(email, oldPassword, newPassword, authToken)
    }

    suspend fun backupToServer(backUpRequest: BackUpRequest) : Response<Result> {
        return RetrofitInstance.api.backupToServer(backUpRequest)
    }

    fun getAllModificationsList() : LiveData<List<Modification>> {
        return repository.getAllModificationsList()
    }

    suspend fun getAllModificationsListWithoutObserver() : List<Modification> {
        return repository.getAllModificationsListWithoutObserver()
    }

    suspend fun importBackupFromServer(email: String) : Response<BackUpRequest> {
        return RetrofitInstance.api.importBackupFromServer(email, preferences.getAuthToken())
    }

    suspend fun clearAllModifications() {
        repository.clearAllModifications()
    }

    suspend fun clearAllGroceryData() {
        repository.clearAllGroceryData()
    }

    suspend fun getMetadata() : Response<MetaDataResponse> {
        return RetrofitInstance.api.getMetadata(preferences.getUserEmail())
    }

    suspend fun getUserMetadata(email: String) : Response<UserMetaDataResponse> {
        return RetrofitInstance.api.getUserMetadata(email, preferences.getAuthToken())
    }

    suspend fun getUserDetails(email: String) : Response<LoginResponse> {
        return RetrofitInstance.api.getUserDetails(email, preferences.getAuthToken())
    }

    suspend fun changeUserName(email: String, name: String) : Response<Result> {
        return RetrofitInstance.api.changeUserName(email, preferences.getAuthToken(), "name", name)
    }

    suspend fun callPaymentFetchApi(planType : String) : Response<PaymentFetchResponse> {
        return RetrofitInstance.api.callPaymentFetchApi(preferences.getUserEmail(), preferences.getAuthToken(), planType)
    }

    suspend fun updatePaymentDetails(planType : String) : Response<Result> {
        return RetrofitInstance.api.updatePaymentDetails(preferences.getUserEmail(), preferences.getAuthToken(), planType)
    }

    suspend fun grantReward() : Response<GrantRewardResponse> {
        return RetrofitInstance.api.grantReward(
            preferences.getUserEmail(),
            preferences.getAuthToken()
        )
    }

    suspend fun buyWithCoins(planType: String) : Response<Result> {
        return RetrofitInstance.api.buyWithCoins(
            preferences.getUserEmail(),
            preferences.getAuthToken(),
            planType
        )
    }
}