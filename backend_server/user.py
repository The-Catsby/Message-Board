import webapp2
from google.appengine.ext import ndb
import db_defs
import json

class User(webapp2.RequestHandler):

	def respond(self, status, payload, message):
		data = { 'payload':payload, 'message':message }
		self.response.status = status
		#self.response.status_message = message
		self.response.write(json.dumps(data))
		return

	#This method validates Username & Password
	#	return: True	if username & passowrd match
	#	return: False	if either var is mismatched
	def authUser(self, username, password):
		query = db_defs.User.query(db_defs.User.username == username).get()	#Query NDB by username
		if(query == None):	#Does user exist? -> No
			return False, None
		else:				#   			-> Yes
			if(password == query.password):	#validate password
				return True, query
			else:
				return False, None

	def createUser(self, username, password):
		query = db_defs.User.query(db_defs.User.username == username).get()
		if(query != None):
			self.respond(400, None, "Invalid request: Username already exists")
			return
		else:
			new_user = db_defs.User()
			if username:
				new_user.username = username
			else:
				self.respond(400,  None, "Invalid request, username is Required")
				return
			if password:
				new_user.password = password
			else:
				self.respond(400,  None, "Invalid request, password is Required")
				return				
			key = new_user.put()
			out = new_user.to_dict()
			self.respond(200,  out, "Success: Logged in as: " + username)
			return			

	def post(self):
		"""	Creates a User entity	
		POST Body Variables:
		username - String Required
		password - String Required
		action - String Required
		"""
		if 'application/json' not in self.request.accept:
			self.respond(406, "Not Acceptable, API only supports application/json MIME type")
			return
		username = self.request.get('username', default_value=None)
		password = self.request.get('password', default_value=None)
		action = self.request.get('action', default_value=None)

		if action == 'login':
			isAuth, user = self.authUser(username, password)
			if(isAuth):
				self.respond(200, user.to_dict(), "Success: Logged in as: " + username)
			else:
				self.respond(400, None, "Error: Log In Failed. Check your Username + Password")
			return
		elif action == 'register':
			self.createUser(username, password)
			return
		# elif action == 'logout':
		# 	self.response.write("logout received")
		# 	return
		else:
			self.respond(400, None, "Action Not Recognized")
			return

	def get(self, **kwargs):
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return
		if 'user_id' in kwargs:
			out = ndb.Key(db_defs.User, int(kwargs['user_id'])).get()
			if not out:
				self.respond(400, None, "User Not Found");		
				return
			self.respond(200, out.to_dict(), "Success")
		else:
			q = db_defs.User.query()
			keys = q.fetch(keys_only=False)
			results = [x.to_dict() for x in keys]
			self.respond(200, results, "Success")

	def delete(self, **kwargs):
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return
		if 'user_id' in kwargs:
			user = ndb.Key(db_defs.User, int(kwargs['user_id'])).get()
			if not user:
				self.response.status = 404
				self.response.status_message = "User Not Found"
				self.response.write(json.dumps(self.response.status, self.response.status_message))
				return
			else:
				user.key.delete()
				self.respond(200, None, "User Deleted")
				return
		else:
			q = db_defs.User.query()
			keys = q.fetch(keys_only=True)
			if keys:
				ndb.delete_multi(keys)
				response = "All Users Deleted"
			else:
				response = "No Users to Delete"
			self.response.write(response)
			return

	# def update(self, **kwargs):
	# 	if 'application/json' not in self.request.accept:
	# 		self.response.status = 406
	# 		self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
	# 		self.response.write(json.dumps(self.response.status, self.response.status_message))
	# 		return
	# 	#Update Specific User
	# 	if 'user_id' in kwargs:
	# 		user = ndb.Key(db_defs.User, int(kwargs['user_id'])).get()
	# 		if not user:
	# 			self.response.status = 404
	# 			self.response.status_message = "User Not Found"
	# 			self.response.write(json.dumps(self.response.status, self.response.status_message))
	# 			return
	# 		else:
	# 			if username = self.request.get('username', default_value=None):
	# 				user.username = username
	# 			if email = self.request.get('email', default_value=None):
	# 				user.email = email
	# 			user.put()
	# 			self.response.write(json.dumps(user.to_dict()))
	# 			return
	# 	#Update All Users
	# 	else:
	# 		self.response.status = 304
	# 		self.response.status_message = "User ID requred"
	# 		self.response.write(json.dumps(self.response.status, self.response.status_message))			
	# 		return

	# 		q = db_defs.User.query()
	# 		keys = q.fetch(keys_only=True)
	# 		if keys:
	# 			ndb.delete_multi(keys)
	# 			response = "All Users Deleted"
	# 		else:
	# 			response = "No Users to Delete"
	# 		self.response.write(response)
	# 		return



# class UserSearch(webapp2.RequestHandler):
# 	def post(self):
# 		"""
# 		Search for Users
# 		POST Body Variables
# 		username = String, usernamename
# 		email = String, email address
# 		"""
# 		if 'application/json' not in self.request.accept:
# 			self.response.status = 406
# 			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
# 			self.response.write(json.dumps(self.response.status, self.response.status_message))
# 			return
# 		q = db_defs.User.query()
# 		if self.request.get('username', None):
# 			q = q.filter(db_defs.User.username == self.request.get('username'))
# 		if self.request.get('email', None):
# 			q = q.filter(db_defs.User.email == self.request.get('email'))
# 		keys = q.fetch(keys_only=True)
# 		results = {'keys':[x.id() for x in keys]}
# 		self.response.write(json.dumps(results))