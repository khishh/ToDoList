# ToDoListApp
Task Management App built with LiveData + Room + MVVM.</br>
During the development of this application, I had paid close attention to what each component in MVVM should be responsible for,
in order to achieve a high level of separation of concerns. By following MVVM architecture, the project remains clean and easily maintainable as
each component has distinctive roles. 
Also, by adopting LiveData with Room database, View components can always display the newest data with less codes and less coupling between View and ViewModel.
Since a View component observe LiveData owned by its ViewModel component, it will be notified whenever any changes occured inside database throughout user interactions.

## Each state of this app
<p>
<img src = "screenshots/HomeFragmentDemo.gif" width ="200" name = "test"/>
<img src = "screenshots/ItemManagementFragmentDemo.gif" width ="200" />
<img src = "screenshots/TabManagementFragmentDemo.gif" width="200"/>
</p>


