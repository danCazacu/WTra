const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');
const proceedSignIn = document.getElementById('proceedSignIn');

signUpButton.addEventListener('click', () => {
	container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
	container.classList.remove("right-panel-active");
});

proceedSignIn.addEventListener('click',()=>{
	window.location = "main.html";
	return false;
});

// function authorize(form) {
// 	document.getElementById("signInEmail");
// 	document.getElementById("signInPassword");
// 	window.location.href = "main.html";
// }

