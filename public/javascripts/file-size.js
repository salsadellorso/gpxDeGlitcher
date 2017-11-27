function addFileSize() {
    var input, file;
    if (!window.FileReader) {
        bodyAppend("p", "The file API isn't supported on this browser yet.");
        return;
    }
    input = document.getElementById('fileinput');
    file = input.files[0];

    sizefield = document.getElementById("fileSize");


    bodyAppend("fileSize", "File " + file.name + " is " + file.size + " bytes in size");
}

function bodyAppend(idName, innerHTML) {
    var elm;
document.cre
    elm = document.createElement(idName);
    elm.innerHTML = innerHTML;
    document.body.appendChild(elm);
}