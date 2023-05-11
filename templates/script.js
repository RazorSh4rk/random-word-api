var converter = new showdown.Converter();
var markdownText = `# Random word API

## Endpoints

### /all
Return all stored words. This will be a huge data dump and will take a long time to fetch and will probably slow down your application.

    Example:
    https://random-word-api.herokuapp.com/all

---

### /word
Return one stored word randomly. Options can be further specified, see the parameters menu.

    Example:
    https://random-word-api.herokuapp.com/word

---

### /languages
Return a list of stored language codes. Useful if you want a multilanguage app. See Github to find out how to add your own. 

    Example:
    https://random-word-api.herokuapp.com/languages

## Parameters

### ?number
    
Set number of requested words. If it exceeds the maximum stored amount, it will just return all of them.

    Example:
    https://random-word-api.herokuapp.com/word?number=10

### ?length
    
Set length of requested words. This will only return words that contain x amount of letters.

    Example:
    https://random-word-api.herokuapp.com/word?length=5

### ?lang
    
Set language of requested words. Go to Github to get more info on how to add your own language. 

    Currently supported languages:
    https://random-word-api.herokuapp.com/languages
    Example:
    https://random-word-api.herokuapp.com/word?lang=es

`
var html = converter.makeHtml(markdownText);
document.getElementById('markdown').innerHTML = html;
