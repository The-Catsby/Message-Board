# Python Backend
This is a RESTful server for the Message Board app

### URL Structure
| Resource | POST | GET | PUT | DELETE |
| -------- | ---- | --- | --- | ------ |
| `/user` | Create new user | List all users | Update all users | Delete all users |
| `/user/<id>` | N/A | Show user | Update user | Delete user |
| `/board` | Create new board | List all boards | Update all boards | Delete all boards |
| `/board/<id>` | N/A | Show board | Update board | Delete board |
| `/post` | N/A | List all posts | Update all posts | Delete all posts |
| `/post/<id>` | N/A | Show post | Update post | Delete post |
| `/board/<id>/user` | N/A | List all subscribed users | Bulk subscribed user update | Delete all subscribed users |
| `/board/<id>/user/<id>` | Subscribe user to board | Show user | Update user | Remove subscribed user |
| `/board/<id>/user/<id>/post` | Create new post in board by user | Show post | Update post | Delete post |
