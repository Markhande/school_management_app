import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.schoolerp.repository.LogoutRepository
import com.example.schoolerp.viewmodel.LogoutViewModel

// ViewModelFactory to create an instance of LogoutViewModel
class LogoutViewModelFactory(private val repository: LogoutRepository) : ViewModelProvider.Factory {

    // Overriding the create method to return an instance of LogoutViewModel
//     fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(LogoutViewModel::class.java)) {
//            return LogoutViewModel(repository) as T // Return the ViewModel instance
//        }
//        throw IllegalArgumentException("Unknown ViewModel class") // If not a valid ViewModel class
//    }
}
