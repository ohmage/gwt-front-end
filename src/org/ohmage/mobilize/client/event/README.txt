EVENTS

These events are used to notify all parts of the app when data is changed or reloaded so
the different pages can refresh their display.

XXXDataChangedEvents are fired by a presenter when it creates, edits, or deletes an XXX item.

UserInfoUpdatedEvent is fired by the MainApp when it refetches the userInfo object to let all the 
presenters know they need to refresh any part of their display that was depending on userInfo. 
(Note that UserInfoUpdatedEvent applies only to data about the currently logged in user, whereas
UserDataChangedEvent is fired for changes made to any user.)

So, for example, when an admin creates a new class, the AdminClassEditPresenter fires a ClassDataChangedEvent
to notify any pages that depend on the class data that they need to reload it. The userInfo obj that is
created by the MainApp and passed to all presenters depends on class data - it stores a list of the
classes the current user belongs to - so when MainApp receives the ClassDataChangedEvent on the EventBus,
it refetches the userInfo object and fires a UserInfoUpdatedEvent to let all the presenters know they 
need to refresh any part of their display that depends on userInfo.
