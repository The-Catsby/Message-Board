import webapp2
from google.appengine.ext import ndb
import db_defs
import json

"""	Helper Functions	"""
def insufficientParams(self, p):
	self.response.status = 400
	self.response.status_message = "Invalid request, " + p + " is Required"
	self.response.write(json.dumps(self.response.status, self.response.status_message))
	return

def notFound(self, p):
	self.response.status = 404
	self.response.status_message = p + " Not Found"
	self.response.write(json.dumps(self.response.status, self.response.status_message))
	return

def isValidRequest(self, r):
	if 'application/json' not in r:
		self.response.status = 406
		self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
		self.response.write(json.dumps(self.response.status, self.response.status_message))
		return False
	else:
		return True

""" Request Handlers	"""

class Post(webapp2.RequestHandler):

	def respond(self, status, payload, message):
		data = { 'payload':payload, 'message':message }
		self.response.status = status
		#self.response.status_message = message
		self.response.write(json.dumps(data))
		return

	def post(self, **kwargs):

		# Validate Request Type
		if not isValidRequest(self, self.request.accept):
			return
		
		# Get Post to Update if Post ID is passed
		if "post_id" in kwargs:
			postkey = ndb.Key(db_defs.Post, int(kwargs['post_id']))
			post = postkey.get()
			if not post:
				self.respond(404, None, "Post Not Found")
				return
			else:
				new_post = post
		# Create a New Post
		else:
			new_post = db_defs.Post()

		title = self.request.get('title', default_value=None)
		content = self.request.get('content', default_value=None)
		
		# Input Validation
		if 'board_id' in kwargs:
			boardkey = ndb.Key(db_defs.Board, int(kwargs['board_id']))
			board = boardkey.get()
			if not board:
				self.respond(404, None, "Board Not Found")
				return
			else:
				new_post.board = boardkey

		if 'user_id' in kwargs:
			userkey = ndb.Key(db_defs.User, int(kwargs['user_id']))
			user = userkey.get()
			if not user:
				self.respond(404, None, "User Not Found")
				return
			else:
				new_post.author = userkey

		if "board_id" not in kwargs or "user_id" not in kwargs:
			insufficientParams(self, "Board ID & User ID")
			return

		if title:
			new_post.title = title
		else:
			self.respond(400, None, "Post Requires a Title")
			return
		if content:
			new_post.content = content
		else:
			self.respond(400, None, "Post Requires Content")
			return

		# Save Post
		new_post.put()
		out = new_post.to_dict()

		# Add Post to Board
		if new_post not in board.posts:
			board.posts.append(new_post)
		# Add Post to User
		if new_post not in user.posts:
			user.posts.append(new_post)

		self.respond(200, out, "Success: Post Created!")
		return

	def get(self, **kwargs):
		
		# Validate Request Type
		if not isValidRequest(self, self.request.accept):
			return

		# Get Post by ID
		if "post_id" in kwargs:
			postkey = ndb.Key(db_defs.Post, int(kwargs['post_id']))
			post = postkey.get()
			if not post:
				notFound(self, "Post (" + kwargs['post_id'] + ")")
				return
			self.response.write(json.dumps(post.to_dict()))
		# List All Posts
		else:
			q = db_defs.Post.query()
			keys = q.fetch(keys_only=True)
			results = {'keys':[x.id() for x in keys]}
			self.response.write(json.dumps(results))

		# Input Validation
		# if 'board_id' in kwargs:
		# 	boardkey = ndb.Key(db_defs.Board, int(kwargs['board_id']))
		# 	board = boardkey.get()
		# 	if not board:
		# 		notFound(self, "Board (" + kwargs['board_id'] + ")")
		# 		return
		# else:
		# 	insufficientParams(self, "Board ID")

		# if 'user_id' in kwargs:
		# 	userkey = ndb.Key(db_defs.User, int(kwargs['user_id']))
		# 	user = userkey.get()
		# 	if not user:
		# 		notFound(self, "Board (" + kwargs['board_id'] + ")")
		# 		return
		# else:
		# 	insufficientParams(self, "Board ID")

	def delete(self, **kwargs):
		
		# Validate Request Type
		if not isValidRequest(self, self.request.accept):
			return

		# Get Post by ID
		if "post_id" in kwargs:
			postkey = ndb.Key(db_defs.Post, int(kwargs['post_id']))
			post = postkey.get()
			if not post:
				notFound(self, "Post (" + kwargs['post_id'] + ")")
				return
			else:
				post.key.delete()
				self.respond(200, None, "Post Deleted!")
				return
		else:
			q = db_defs.Post.query()
			keys = q.fetch(keys_only=True)
			if keys:
				ndb.delete_multi(keys)
				response = "All Posts Deleted"
			else:
				response = "No Posts to Delete"
			self.response.write(json.dumps(response))
			return

	def put(self, **kwargs):

		# Validate Request Type
		if not isValidRequest(self, self.request.accept):
			return
		
		# Get Post to Update if Post ID is passed
		if "post_id" in kwargs:
			postkey = ndb.Key(db_defs.Post, int(kwargs['post_id']))
			post = postkey.get()
			if not post:
				self.respond(404, None, "Post Not Found")
				return
			else:
				new_post = post
		else:
			self.respond(400, None, "Need Post ID")

		title = self.request.get('title', default_value=None)
		content = self.request.get('content', default_value=None)
		
		if title:
			new_post.title = title
		else:
			self.respond(400, None, "Post Requires a Title")
			return
		if content:
			new_post.content = content
		else:
			self.respond(400, None, "Post Requires Content")
			return

		# Save Post
		new_post.put()
		out = new_post.to_dict()

		self.respond(200, out, "Success: Post Updated!")
		return