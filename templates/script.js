const section_names = [
    "endpoints",
    "parameters",
    "license",
    "donate"
]

const sections = section_names.map(el => document.getElementById(el))
const buttons = section_names.map(el => document.getElementById(el + "_btn"))

buttons.forEach(el => el.addEventListener("click", evt => {
    const id = evt.target.id.replace("_btn", "")
    sections.forEach(el => {
        if(el.id == id) el.style.display = "block"
        else el.style.display = "none"
    })
}))

let copy = (text) => navigator.clipboard.writeText(text)

// show endpoints by default
sections.forEach(el => {
    if(el.id == "endpoints") el.style.display = "block"
    else el.style.display = "none"
})

fetch("https://random-word-api.herokuapp.com/languages")
    .then(r => r.json())
    .then(r => document.getElementById("langs").textContent = r)