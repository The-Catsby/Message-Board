import webapp2
from google.appengine.ext import ndb
import db_defs
import json

class Board(webapp2.RequestHandler):
	
	def respond(self, status, payload, message):
		data = { 'payload':payload, 'message':message }
		self.response.status = status
		#self.response.status_message = message
		self.response.write(json.dumps(data))
		return

	def post(self):
		"""	Creates a message Board entity	
		POST Body Variables:
		name - Required, Board name
		users[] - Array of Mod ids
		posts[] - Array of channel posts
		"""
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return

		name = self.request.get('name', default_value=None)
		users = self.request.get_all('users[]', default_value=None)
		posts = self.request.get_all('posts[]', default_value=None)

		#Check Board does not already exist
		query = db_defs.Board.query(db_defs.Board.name == name).get()
		if(query != None):
			self.respond(400, None, "Invalid request: Board already exists")
			return
		else:
			new_board = db_defs.Board()
			if name:
				new_board.name = name
			else:
				self.respond(400, None, "Invalid request, Name is Required")
				return
			if users:
				for user in users:
					new_board.users.append(ndb.Key(db_defs.User, int(user)))
			if posts:
				new_board.posts = posts
			key = new_board.put()
			out = new_board.to_dict()
			self.respond(200, out, "Success: " + name + " Board Created!")
			return

	def get(self, **kwargs):
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return
		if 'board_id' in kwargs:
			out = ndb.Key(db_defs.Board, int(kwargs['board_id'])).get()
			if out == None:
				self.respond(400, None, "Board Not Found")
				return
			else:	
				#Get all Posts with this Board ID
				key = ndb.Key(db_defs.Board, int(kwargs['board_id']))
				query = db_defs.Post.query(db_defs.Post.board == key).fetch()
				results = [x.to_dict() for x in query]
				self.respond(200, results, "Success")
				return
		else:
			q = db_defs.Board.query()
			keys = q.fetch(keys_only=False)
			results = [ x.to_dict() for x in keys]
			self.respond(200, results, "Success")

	def delete(self, **kwargs):
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return
		if 'board_id' in kwargs:
			board = ndb.Key(db_defs.Board, int(kwargs['board_id'])).get()
			if not board:
				self.response.status = 404
				self.response.status_message = "Board Not Found"
				self.response.write(json.dumps(self.response.status, self.response.status_message))
				return
			else:
				board.key.delete()
				self.response.write("Board deleted")
				return
		else:
			q = db_defs.Board.query()
			keys = q.fetch(keys_only=True)
			if keys:
				ndb.delete_multi(keys)
				response = "All Boards Deleted"
			else:
				response = "No Boards to Delete"
			self.response.write(response)
			return


class BoardUsers(webapp2.RequestHandler):
	
	def put(self, **kwargs):
		
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return
		
		if 'board_id' in kwargs:
			board = ndb.Key(db_defs.Board, int(kwargs['board_id'])).get()
			if not board:
				self.response.status = 404
				self.response.status_message = "Board Not Found"
				self.response.write(json.dumps(self.response.status, self.response.status_message))
				return
		
		if 'user_id' in kwargs:
			userkey = ndb.Key(db_defs.User, int(kwargs['user_id']))
			user = userkey.get()
			if not user:
				self.response.status = 404
				self.response.status_message = "User Not Found"
				self.response.write(json.dumps(self.response.status, self.response.status_message))
				return
		
		if userkey not in board.users:
			board.users.append(userkey)
			board.put()
		else:
			self.response.status = 304
			self.response.status_message = "Not Modified: User is already subscribed to Board"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return			
		
		self.response.write(json.dumps(board.to_dict()))
		return

	def delete(self, **kwargs):
		if 'application/json' not in self.request.accept:
			self.response.status = 406
			self.response.status_message = "Not Acceptable, API only supports application/json MIME type"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return
		
		if 'board_id' in kwargs:
			board = ndb.Key(db_defs.Board, int(kwargs['board_id'])).get()
			if not board:
				self.response.status = 404
				self.response.status_message = "Board Not Found"
				self.response.write(json.dumps(self.response.status, self.response.status_message))
				return
		
		if 'user_id' in kwargs:
			userkey = ndb.Key(db_defs.User, int(kwargs['user_id']))
			user = userkey.get()
			if not user:
				self.response.status = 404
				self.response.status_message = "User Not Found"
				self.response.write(json.dumps(self.response.status, self.response.status_message))
				return
		
		if userkey in board.users:
			board.users.remove(userkey)
			board.put()
		else:
			self.response.status = 304
			self.response.status_message = "Not Modified: User is not subscribed to Board"
			self.response.write(json.dumps(self.response.status, self.response.status_message))
			return			
		
		self.response.write(json.dumps(board.to_dict()))
		return