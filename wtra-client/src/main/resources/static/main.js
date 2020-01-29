const home = document.getElementById('home');
const yourUploads = document.getElementById('yourUploads');
const trafficSigns = document.getElementById('trafficSigns');
const contact = document.getElementById('contact');
const about = document.getElementById('about');
const logout = document.getElementById('logout');

function highlight(button) {
    home.className = "inactive";
    yourUploads.className = "inactive";
    trafficSigns.className = "inactive";
    contact.className = "inactive";
    about.className = "inactive";
    logout.className = "inactive";
    button.className = "active";
    if (button === logout) {
        document.cookie = "logout=true";
        document.location = "index.html";
    }
}