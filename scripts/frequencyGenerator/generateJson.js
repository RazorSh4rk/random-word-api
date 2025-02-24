const fs = require('fs');
const axios = require('axios');

const getFrequencyFromWikipedia = async (word) => {
    const url = `https://${lang}.wikipedia.org/w/api.php?action=query&list=search&srsearch=${word}&format=json&origin=*`;
    try {
        const response = await axios.get(url);
        return response.data.query.search.length > 0 ? response.data.query.search[0].size : 0;
    } catch (error) {
        console.error(`Error on getting the frequency for: "${word}":`, error.message);
        return 0; // In case of error he gets the frequency as 0
    }
};


const generateJson = async () => {
    console.log("Enter the language of the words (en, it, fr...): ");
    let lang = prompt();
    console.log("Enter the path to the json file: ");
    let path = prompt();
    const data = fs.readFileSync(path, 'utf8');
    const jsonData = JSON.parse(data);

    const frequencyData = [];
    const totalWords = jsonData.length;

    for (let i = 0; i < totalWords; i++) {
        const word = jsonData[i];
        const frequency = await getFrequencyFromWikipedia(word);
        frequencyData.push({ word, frequency });
        
    }

    const jsonString = JSON.stringify(frequencyData, null, 2);

    fs.writeFileSync(lang+'Frequency.json', jsonString);
    console.log("File JSON created succesfully: "+ lang+"Frequency.json");
};

generateJson();
