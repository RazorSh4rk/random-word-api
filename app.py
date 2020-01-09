import json, os, string, random
from flask import Flask, Response, request, render_template
from flask_cors import cross_origin
from tinydb import TinyDB, Query
from random import randint

db = TinyDB('keys/api_keys.json')
if(db.all() == []):
    db.insert({'key' : 'yecgaa'})

app = Flask(__name__)
app.secret_key = os.urandom(12)

words_json = json.loads( open('words.json', 'r').read() )
swear_json = json.loads( open('swear.json', 'r').read() )

@app.route('/')
def _index():
    return render_template('index.html')

@app.route('/key')
def _key():
    key = ''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(8))
    db.insert({'key' : key})
    return key

@app.route('/word')
@cross_origin()
def _word():
    key = request.args.get('key')

    #filter swear words, default to off
    try:
        swear = int(request.args.get('swear'))
    except:
        swear = 0
    if swear == 0:
        dictionary = words_json
    if swear == 1:
        dictionary = words_json + swear_json
        
    try:
        number = int(request.args.get('number'))
    except:
        number = 1
        
    rec = Query()
    if(db.search(rec.key == key) != []):
        all_words = []
            
        for i in range(0, number):
            word = dictionary[randint(0, len(dictionary) - 1)]
            all_words.append(word)
        return Response(json.dumps(all_words), mimetype='application/json')
        
    return Response('"wrong API key"', mimetype='application/json')

@app.route('/all')
@cross_origin()
def _all():
    key = request.args.get('key')

    #filter swear words, default to off
    try:
        swear = int(request.args.get('swear'))
    except:
        swear = 0
        
    if swear == 0:
        dictionary = words_json
    if swear == 1:
        dictionary = words_json + swear_json
        
    rec = Query()
    if(db.search(rec.key == key) != []):
        return Response(json.dumps(dictionary), mimetype='application/json')
        
    return Response('"wrong API key"', mimetype='application/json')


if __name__ == '__main__':
    app.run(debug=False, use_reloader=True)