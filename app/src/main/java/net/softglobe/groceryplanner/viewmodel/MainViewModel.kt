package net.softglobe.groceryplanner.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.softglobe.groceryplanner.model.BackUpRequest
import net.softglobe.groceryplanner.model.network.LoginResponse
import net.softglobe.groceryplanner.model.Grocery
import net.softglobe.groceryplanner.model.GroceryDao
import net.softglobe.groceryplanner.model.GroceryDatabase
import net.softglobe.groceryplanner.model.Modification
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.model.Repository
import net.softglobe.groceryplanner.model.network.Result
import net.softglobe.groceryplanner.model.network.RetrofitInstance
import net.softglobe.groceryplanner.model.network.User
import retrofit2.Response

class MainViewModel(context: Context) : ViewModel() {
    private var groceryDao : GroceryDao
    private var repository : Repository
    private var preferences : Preferences

    init {
        groceryDao = GroceryDatabase.getInstance(context).groceryDao
        repository = Repository(groceryDao)
        preferences = Preferences(context)
    }

    fun getGroceryList() : LiveData<List<Grocery>> {
        return repository.getGroceryList()
    }

    suspend fun getGroceryListWithoutObserver() : List<Grocery> {
        return repository.getGroceryListWithoutObserver()
    }

    fun getModificationsList(id : Int) : LiveData<List<Modification>> {
        return repository.getModificationsList(id)
    }

    fun insertGroceryItem(grocery: Grocery, modification: Modification) : Long {
        var id = 0L
        viewModelScope.launch(Dispatchers.IO) {
            id = repository.insertGroceryItem(grocery)
            modification.itemId = id.toInt()
            val modificationCount = repository.getModificationsCountByGroceryItemId(id.toInt())
            if (!preferences.isPaidUser() && modificationCount < preferences.getModificationsRecordsLimit())
                repository.insertModification(modification)
        }
        return id
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

    suspend fun changePassword(email: String, oldPassword : String, newPassword : String) : Response<Result> {
        return RetrofitInstance.api.changePassword(email, oldPassword, newPassword)
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
        return RetrofitInstance.api.importBackupFromServer(email)
    }

    suspend fun clearAllModifications() {
        repository.clearAllModifications()
    }

    suspend fun clearAllGroceryData() {
        repository.clearAllGroceryData()
    }
}