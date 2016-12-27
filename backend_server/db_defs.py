from google.appengine.ext import ndb

""" Assignment 3 """

class Model(ndb.Model):
	def to_dict(self):
		d = super(Model, self).to_dict()
		d['key'] = self.key.id()
		return d

class Update(Model):
	date_time = ndb.DateTimeProperty(required=True)
	user_count = ndb.IntegerProperty(required=True)
	message_count = ndb.IntegerProperty(required=True)

class Post(Model):
	title = ndb.StringProperty(required=True)
	content = ndb.StringProperty(required=True)
	board = ndb.KeyProperty(required=True)
	author = ndb.KeyProperty(required=True)
	
	def to_dict(self):
		d = super(Post, self).to_dict()
		d['board'] = self.board.id()
		d['author'] = self.author.id()
		return d

class Board(Model):
	name = ndb.StringProperty(required=True)
	posts = ndb.StructuredProperty(Post, repeated=True)		
	users = ndb.KeyProperty(repeated=True)
	updates = ndb.StructuredProperty(Update, repeated=True)

	def to_dict(self):
		d = super(Board, self).to_dict()
		d['users'] = [u.id() for u in d['users']]
		return d

class User(Model):
	username = ndb.StringProperty(required=True)
	password = ndb.StringProperty(required=True)
	posts = ndb.StructuredProperty(Post, repeated=True)		
