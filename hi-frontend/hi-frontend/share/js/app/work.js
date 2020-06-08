var hoursValue = ['','05:00','05:30','06:00','06:30','07:00','07:30','08:00','08:30','09:00','09:30','10:00','10:30','11:00','11:30','12:00','12:30','13:00','13:30','14:00','14:30','15:00','15:30','16:00','16:30','17:00','17:30','18:00','18:30','19:00','19:30','20:00','20:30','21:00','21:30','22:00','22:30'];
var innerHour = '';
for(var i = 0; i < hoursValue.length; i++) {
    innerHour += '<option value="' + hoursValue[i] + '">' + hoursValue[i] + '</option>';
}

searchWork = function() {
    $('#fromDate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#toDate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    document.getElementById('fromHour').innerHTML = innerHour;
    document.getElementById('toHour').innerHTML = innerHour;
    $('#workTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchwork',
            data: function ( d ) {
                d.maidName = $('#maidName').val();
                d.fromDate = $('#fromDate').val();
                d.toDate = $('#toDate').val();
                d.dayOfWeek = $('#dayOfWeek').val();
                d.fromHour = $('#fromHour').val();
                d.toHour = $('#toHour').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.workId = $('#workTable_wrapper :input[type="checkbox"]').serialize();
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
    var arrCheckbox = document.getElementsByName('workId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('workId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deleteWork = function() {
    document.getElementById('isdelete').value = '1';
    searchWork();
}

viewAddWork = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddwork',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addWork();
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
    var arrCheckbox = document.getElementsByName('workId');
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
                    deleteWork();
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

addWork = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addwork',
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
        url: '/backlistwork',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchWork();
                    return false;
                } else {
                    return true;
                }
            });            
            searchWork();
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