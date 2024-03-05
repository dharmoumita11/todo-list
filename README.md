# todo-list
A simple todo list backend service

***
Allow deletion of items assuming, an item might not be relevant anymore for the user, deletion should be allowed. 
However, to prevent deletion by mistake, an additional confirmation prompt can be added on the front end.


***
- Add authentication for swagger
- use custom paths for swagger-ui as well as api-docs

***
Error handling can be improved by adding
- ErrorCode Enum containing
  - errorCode
- default message and/or explanation
- A default or parent custom exception containing
  - errorCode
  - errorMessage
  - detailedMessage
- All the other custom exceptions will extend this default or parent custom exception 
