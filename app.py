import json, os, string, random
from flask import Flask, Response, request, render_template
from flask_cors import cross_origin
from random import randint

app = Flask(__name__)
app.secret_key = os.urandom(12)

words_json = json.loads( open('words.json', 'r').read() )
swear_json = json.loads( open('swear.json', 'r').read() )

@app.route('/')
def _index():
    return render_template('index.html')

@app.route('/word')
@cross_origin()
def _word():
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

    all_words = []
            
    for _ in range(0, number):
        word = dictionary[randint(0, len(dictionary) - 1)]
        all_words.append(word)
    return Response(json.dumps(all_words), mimetype='application/json')

@app.route('/all')
@cross_origin()
def _all():
    #filter swear words, default to off
    try:
        swear = int(request.args.get('swear'))
    except:
        swear = 0
        
    if swear == 0:
        dictionary = words_json
    if swear == 1:
        dictionary = words_json + swear_json
        
    return Response(json.dumps(dictionary), mimetype='application/json')

if __name__ == '__main__':
    app.run(debug=False, use_reloader=True)
