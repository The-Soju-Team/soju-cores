setParam = function(obj) {
    closeAllFieldError();
    if(document.getElementById('fromdate').value == '') {
        showFieldError('fromdate','Hãy nhập từ ngày');
        return false;        
    }
    if(document.getElementById('todate').value == '') {
        showFieldError('todate','Hãy nhập đến ngày');
        return false;
    }        
    
    obj.href = '/exportbusiness?type=' + document.getElementById('type').value 
            + '&fromdate=' + document.getElementById('fromdate').value
            + '&todate=' + document.getElementById('todate').value;
    return true;
}

showFieldError = function(fieldId, message) {
    document.getElementById(fieldId).className = 'form-control error';
    document.getElementById(fieldId + '-require').style.display = '';
    document.getElementById(fieldId + '-require').innerHTML = message;
}

closeAllFieldError = function() {
    var arrControl = document.getElementsByClassName('form-control');
    var arrErrorLabel = document.getElementsByClassName('error');
    for(var i = 0; i < arrControl.length; i++) arrControl[i].className = 'form-control';
    for(var i = 0; i < arrErrorLabel.length; i++) arrErrorLabel[i].style.display = 'none';
}