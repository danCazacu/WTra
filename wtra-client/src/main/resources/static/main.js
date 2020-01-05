const home = document.getElementById('Home');
const news = document.getElementById('News');
const contact = document.getElementById('Contact');
const about = document.getElementById('About');
const logout = document.getElementById('Logout');

function highlight(button) {
    home.className = "inactive";
    news.className = "inactive";
    contact.className = "inactive";
    about.className = "inactive";
    logout.className = "inactive";
    button.className = "active";
    if (button === logout) {
        document.cookie = "logout=true";
        document.location = "index.html";
    }
}