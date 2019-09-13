import cherrypy
import os, glob
	  
class FileIterator(object):
    chunk_size = 4096
    
    def __init__(self, filename):
        self.filename = filename
        self.fileobj = open(self.filename, 'rb')
        
    def __iter__(self):
        return self
        
    def next(self):
        chunk = self.fileobj.read(self.chunk_size)
        while len(chunk) > 0:
            yield chunk
            chunk = self.fileobj.read(self.chunk_size)

      
class TinyServer(object):
    def downloadPlaylist(self):
        musicList = glob.glob('music/*.mp3')
        return '\n'.join([os.path.basename(f) for f in musicList])
    downloadPlaylist.exposed = True
    
    def downloadMusic(self, basename):
        filename = os.path.join('music', basename)
        data = FileIterator(filename).next()
        return data
    downloadMusic.exposed = True
        

cherrypy.quickstart(TinyServer())