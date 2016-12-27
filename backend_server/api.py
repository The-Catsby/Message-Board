import webapp2

config = {'default-group':'base-data'}

app = webapp2.WSGIApplication([
	('/user', 'user.User'),
], debug=True)
app.router.add(webapp2.Route(r'/user/<user_id:[0-9]+><:/?>', 'user.User'))
# app.router.add(webap1p2.Route(r'/user/search', 'user.UserSearch'))

app.router.add(webapp2.Route(r'/board', 'board.Board'))
app.router.add(webapp2.Route(r'/board/<board_id:[0-9]+><:/?>', 'board.Board'))
app.router.add(webapp2.Route(r'/board/<board_id:[0-9]+>/user/<user_id:[0-9]+><:/?>', 'board.BoardUsers'))

app.router.add(webapp2.Route(r'/post', 'post.Post'))
app.router.add(webapp2.Route(r'/post/<post_id:[0-9]+><:/?>', 'post.Post'))
app.router.add(webapp2.Route(r'/board/<board_id:[0-9]+>/user/<user_id:[0-9]+>/post', 'post.Post'))

