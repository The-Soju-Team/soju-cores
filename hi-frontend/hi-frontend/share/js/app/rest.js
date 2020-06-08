var hoursValue = ['','00:00','00:15','00:30','00:45','01:00','01:15','01:30','01:45','02:00','02:15','02:30','02:45','03:00','03:15','03:30','03:45','04:00','04:15','04:30','04:45','05:00','05:15','05:30','05:45','06:00','06:15','06:30','06:45','07:00','07:15','07:30','07:45','08:00','08:15','08:30','08:45','09:00','09:15','09:30','09:45','10:00','10:15','10:30','10:45','11:00','11:15','11:30','11:45','12:00','12:15','12:30','12:45','13:00','13:15','13:30','13:45','14:00','14:15','14:30','14:45','15:00','15:15','15:30','15:45','16:00','16:15','16:30','16:45','17:00','17:15','17:30','17:45','18:00','18:15','18:30','18:45','19:00','19:15','19:30','19:45','20:00','20:15','20:30','20:45','21:00','21:15','21:30','21:45','22:00','22:15','22:30','22:45','23:00','23:15','23:30','23:45'];
var innerHour = '';
for(var i = 0; i < hoursValue.length; i++) {
    innerHour += '<option value="' + hoursValue[i] + '">' + hoursValue[i] + '</option>';
}

searchRest = function() {
    $('#fromDate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#toDate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    document.getElementById('fromHour').innerHTML = innerHour;
    document.getElementById('toHour').innerHTML = innerHour;
    $('#restTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchrest',
            data: function ( d ) {
                d.maidName = $('#maidName').val();
                d.fromDate = $('#fromDate').val();
                d.toDate = $('#toDate').val();
                d.dayOfWeek = $('#dayOfWeek').val();
                d.fromHour = $('#fromHour').val();
                d.toHour = $('#toHour').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.restId = $('#restTable_wrapper :input[type="checkbox"]').serialize();
                }
                else d.isdelete = '0';
            },
            cache: false
        },
        initComplete: function(settings, json) {
            if(document.getElementById('isdelete').value == '1') {
                successMessage('Xóa thành công!');
            }
            document.getElementById('isdelete').value = '0';
            validateCheckAll();
        },        
        language: {
            'decimal':        '',
            'emptyTable':     'Không có dữ liệu',
            'info':           'Từ _START_ đến _END_ của _TOTAL_ bản ghi',
            'infoEmpty':      'Từ 0 đến 0 của 0 bản ghi',
            'infoFiltered':   '(Lọc từ _MAX_ tổng số bản ghi)',
            'infoPostFix':    '',
            'thousands':      ',',
            'lengthMenu':     'Hiển thị _MENU_ dòng/trang',
            'loadingRecords': 'Đang tải...',
            'processing':     'Đang xử lý...',
            'search':         'Tìm kiếm:',
            'zeroRecords':    'Không tìm thấy dữ liệu',
            'paginate': {
                'first':      'Đầu',
                'last':       'Cuối',
                'next':       'Tiếp',
                'previous':   'Trước'
            },
            'aria': {
                'sortAscending':  ': Sắp xếp tăng dần',
                'sortDescending': ': Sắp xếp giảm dần'
            }
        }
    } );
}

checkAll = function (obj) {
    var arrCheckbox = document.getElementsByName('restId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('restId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deleteRest = function() {
    document.getElementById('isdelete').value = '1';
    searchRest();
}

viewAddRest = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddrest',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addRest();
                    return false;
                } else {
                    return true;
                }
            });
            
            var innerMaid = '<option value=""></option>';
            for(var i = 0; i < data.maid.length; i++) {
                innerMaid += '<option value="' + data.maid[i].user_id + '">' + data.maid[i].name + '</option>';
            }
            document.getElementById('maidId').innerHTML = innerMaid;
            $('#fromDate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
            $('#toDate').datepicker({pickTime: false, format: "dd-mm-yyyy" });            

            document.getElementById('monStart').innerHTML = innerHour;
            document.getElementById('monEnd').innerHTML = innerHour;
            document.getElementById('tueStart').innerHTML = innerHour;
            document.getElementById('tueEnd').innerHTML = innerHour;
            document.getElementById('wedStart').innerHTML = innerHour;
            document.getElementById('wedEnd').innerHTML = innerHour;
            document.getElementById('thuStart').innerHTML = innerHour;
            document.getElementById('thuEnd').innerHTML = innerHour;
            document.getElementById('friStart').innerHTML = innerHour;
            document.getElementById('friEnd').innerHTML = innerHour;
            document.getElementById('satStart').innerHTML = innerHour;
            document.getElementById('satEnd').innerHTML = innerHour;
            document.getElementById('sunStart').innerHTML = innerHour;
            document.getElementById('sunEnd').innerHTML = innerHour;
            
            document.getElementById('maidId').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('restId');
    var check = false;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(arrCheckbox[i].checked) {
            check = true;
            break;
        }
    }
    if(check) confirmDelete();
    else errorMessage('Bạn chưa chọn bản ghi để xóa!');
}

confirmDelete = function() {
    noty({
        text: 'Bạn có muốn xóa không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    deleteRest();
                }},
                {addClass: 'btn btn-info btn-clean', text: 'Hủy bỏ', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

errorMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đóng', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

successMessage = function(text) {
    noty({
        text: text,
        layout: 'center',
        type: 'success'
    });  
    setTimeout(function(){ closeAllMessage(); }, 2000);
}

addRest = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addrest',
            data: $("#addForm").serialize(),
            cache: false, //fix loop IE
            success: function(data, textStatus, jqXHR) {
                closeAllMessage();
                successMessage('Thêm mới thành công!');
                clearForm();
            }
        });
    }
}

validateClient = function() {
    closeAllMessage();
    if(document.getElementById('maidId').value == null || document.getElementById('maidId').value.trim().length == 0) {
        showFieldError('maidId','Hãy nhập Giúp việc');
        return false;
    } else if(document.getElementById('fromDate').value == null || document.getElementById('fromDate').value.trim().length == 0) {
        showFieldError('fromDate','Hãy nhập Ngày bắt đầu');
        return false;
    } else if(document.getElementById('toDate').value == null || document.getElementById('toDate').value.trim().length == 0) {
        showFieldError('toDate','Hãy nhập Ngày kết thúc');
        return false;
    } 
    return true;
}

closeAllMessage = function() {
    $.noty.closeAll();
    closeAllFieldError();
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

clearForm = function() {
    document.getElementById('maidId').value = '';
    document.getElementById('fromDate').value = '';
    document.getElementById('toDate').value = '';
    document.getElementById('monStart').value = '';
    document.getElementById('monEnd').value = '';
    document.getElementById('tueStart').value = '';
    document.getElementById('tueEnd').value = '';
    document.getElementById('wedStart').value = '';
    document.getElementById('wedEnd').value = '';
    document.getElementById('thuStart').value = '';
    document.getElementById('thuEnd').value = '';
    document.getElementById('friStart').value = '';
    document.getElementById('friEnd').value = '';
    document.getElementById('satStart').value = '';
    document.getElementById('satEnd').value = '';
    document.getElementById('sunStart').value = '';
    document.getElementById('sunEnd').value = '';
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistrest',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchRest();
                    return false;
                } else {
                    return true;
                }
            });            
            searchRest();
        }
    }); 
}

chooseUser = function(obj) {
    document.getElementById('maidId').value = obj.value;
    var userName = obj.parentNode.parentNode.getElementsByTagName('td')[1];
    document.getElementById('maid').value = userName.innerHTML;
    $('#selectModal').modal('hide');
}

openPopupUser = function() {
    $('#searchForm').keypress(function (e) {
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            searchPopup();
            return false;
        } else {
            return true;
        }
    });            
    searchPopup();
}