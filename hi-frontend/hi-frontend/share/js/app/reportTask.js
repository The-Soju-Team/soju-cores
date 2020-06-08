setParam = function(obj) {    
    var linkHref = '/exporttask?t=1';
    if(document.getElementById('fromdate').value != '') 
        linkHref += '&fromdate=' + document.getElementById('fromdate').value;
    if(document.getElementById('todate').value != '') 
        linkHref += '&todate=' + document.getElementById('todate').value;
        
    obj.href = linkHref;
    return true;
}
