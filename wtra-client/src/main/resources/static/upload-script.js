const uploadButton = document.getElementById('uploadButton');
const uploadForm = document.getElementById('uploadForm');

uploadButton.addEventListener('click', () => {
    var video = document.getElementById("fileUpload").files[0];
var req = new XMLHttpRequest();
var formData = new FormData(uploadForm);

req.open("POST", 'http://wtra-api.eu-west-1.elasticbeanstalk.com/videos');

//the code below tries to modifiy some text in UI of the file was uploaded or not,
// problem is that the events are not called. Meaning that we cannot get the response
// req.onreadystatechange = function(){
//   if (req.status === 200) {
//     uploadForm.getElementById("fileUploadText").innerHTML = "File uploaded succesfully!";
//   }else{
//     uploadForm.getElementById("fileUploadText").innerHTML = "Something went wrong. Try again later!";
//   }
// };
// req.onload = function(){
//   if (req.status === 200) {
//     uploadForm.getElementById("fileUploadText").innerHTML = "File uploaded succesfully!";
//   }else{
//     uploadForm.getElementById("fileUploadText").innerHTML = "Something went wrong. Try again later!";
//   }
// };
// req.onloadstart = function(){
//   if (req.status === 200) {
//     uploadForm.getElementById("fileUploadText").innerHTML = "File uploaded succesfully!";
//   }else{
//     uploadForm.getElementById("fileUploadText").innerHTML = "Something went wrong. Try again later!";
//   }
// };
// req.onloadend = function(){
//   if (req.status === 200) {
//     uploadForm.getElementById("fileUploadText").innerHTML = "File uploaded succesfully!";
//   }else{
//     uploadForm.getElementById("fileUploadText").innerHTML = "Something went wrong. Try again later!";
//   }
// };
req.send(formData);
})
;


$(document).ready(function () {
    $('form input').change(function () {
        $('form p').text(this.files.length + " file(s) selected");
    });
});