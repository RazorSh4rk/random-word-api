# random-word-api
A Scala rest API to serve random words

It's alive at [https://https://random-word-api.herokuapp.com/](here) and [https://https://random-word-api2.herokuapp.com/](here)

## Let's get straight to the point - How do I make it run?
Use `sbt run`!
Uses Cask

## How do I get my random words?
If you want to get a random word, send a GET request to `/word?number=10`.
Replace 10 with the amount of words you want to recieve.
For example, this is a request to `/word?number=3`
![Image](https://cdn.discordapp.com/attachments/749642442172530732/764157382862569481/unknown.png)

If you want to get every word possible, send a GET request to `/all`

If you want to get a random word but filter out swear words send a GET request to (razor add here - `/word?number=5?swear=0` doesn't work, im bad at URLs.)

## How does it work?
(razor add here)
