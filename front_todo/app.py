from flask import Flask, render_template

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

if __name__ == '__main__':
    app.run(host='host', port=9000, ssl_context=('cert.pem', 'key.pem'))
