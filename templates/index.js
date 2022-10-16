function endpoints()
{
    document.getElementById("content").innerHTML = `<h2>/all</h2>
    <div class="content-div">
        <p>Returns all stored words. This is a huge data dump which will probably slow down your application.</p>
        <div class="content-example">
            <p onclick="window.location.replace('https://random-word-api.herokuapp.com/all');">https://random-word-api.herokuapp.com/all</p>
            <h4 onclick="navigator.clipboard.writeText('https://random-word-api.herokuapp.com/all');">Copy</h4>
        </div>
    </div>
    <h2>/word</h2>
    <div class="content-div">
        <p>Randomly returns one stored word. Options can be further specified, see the parameters page.</p>
        <div class="content-example">
            <p onclick="window.location.replace('https://random-word-api.herokuapp.com/word');">https://random-word-api.herokuapp.com/word</p>
            <h4 onclick="navigator.clipboard.writeText('https://random-word-api.herokuapp.com/word');">Copy</h4>
        </div>
    </div>
    <h2>/languages</h2>
    <div class="content-div">
        <p>Returns a list of stored language codes. Useful if you want a multi-language app. See the Github to find out how to add your own.</p>
        <div class="content-example">
            <p onclick="window.location.replace('https://random-word-api.herokuapp.com/languages');">https://random-word-api.herokuapp.com/languages</p>
            <h4 onclick="navigator.clipboard.writeText('https://random-word-api.herokuapp.com/languages');">Copy</h4>
        </div>
    </div>`;
}
function parameters()
{
    document.getElementById("content").innerHTML = `<h2>?number</h2>
    <div class="content-div">
        <p>Sets the number of requested words. If it exceeds the stored amount, it will just return all of them.</p>
        <div class="content-example">
            <p onclick="window.location.replace('https://random-word-api.herokuapp.com/word?number=10');">https://random-word-api.herokuapp.com/word?number=10</p>
            <h4 onclick="navigator.clipboard.writeText('https://random-word-api.herokuapp.com/word?number=10');">Copy</h4>
        </div>
    </div>
    <h2>?length</h2>
    <div class="content-div">
        <p>Sets the length of requested words. This will only return words that are the specified length.</p>
        <div class="content-example">
            <p onclick="window.location.replace('https://random-word-api.herokuapp.com/word?length=5');">https://random-word-api.herokuapp.com/word?length=5</p>
            <h4 onclick="navigator.clipboard.writeText('https://random-word-api.herokuapp.com/word?length=5');">Copy</h4>
        </div>
    </div>
    <h2>?lang</h2>
    <div class="content-div">
        <p>Sets the language of requested words. The currently supported languages includes: <i>zh</i>, <i>de</i>, <i>es</i>, and <i>it</i>.</p>
        <div class="content-example">
            <p onclick="window.location.replace('https://random-word-api.herokuapp.com/word?lang=es');">https://random-word-api.herokuapp.com/word?lang=es</p>
            <h4 onclick="navigator.clipboard.writeText('https://random-word-api.herokuapp.com/word?lang=es');">Copy</h4>
        </div>
    </div>`;
}
function about()
{
    document.getElementById("content").innerHTML = `<h2>About</h2>
    <div class="content-div">
        <p>A thorough collection of words scraped from websites. Self-hosting from the source is supported. It is now running on a hobby dyno, so it should be up quite reliably. Runs under the <b onclick="window.location.replace('https://choosealicense.com/licenses/wtfpl/');">DWTFYW license</b>.</p>
    </div>`;
}