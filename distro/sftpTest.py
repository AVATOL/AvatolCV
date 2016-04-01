import pysftp

def main():
    conn = pysftp.Connection(host='flip.engr.oregonstate.edu', username='irvineje', password='Netonthis()')
    foo = conn.listdir()
    print foo
    #sftp.put('/my/local/filename')  # upload file to public/ on remote
    conn.chdir('/nfs/stak/students/i/irvineje/public_html/AvatolCV/')
    foo = conn.listdir()
    print ""
    print ""
    print foo
    #sftp.get('links.txt', 'links.txt')    # get file
    #sftp.get('/nfs/stak/students/i/irvineje/public_html/AvatolCV/links.txt', 'links.txt')    # get file
    conn.close()


if __name__ == "__main__":
   main()      