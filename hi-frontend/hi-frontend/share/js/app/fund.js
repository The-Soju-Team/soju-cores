searchFund = function() {
    $('#fundTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        columnDefs: [
            {
                targets: 2,
                render: function (data, type, full, meta) {
                    if (data) return separate(data.toString());
                    return data;
                }
            } 
        ],
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchfund',
            data: function ( d ) {
                d.fundName = $('#fundName').val();
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.fundId = $('#fundTable_wrapper :input[type="checkbox"]').serialize();
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
    var arrCheckbox = document.getElementsByName('fundId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('fundId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deleteFund = function() {
    document.getElementById('isdelete').value = '1';
    searchFund();
}

viewAddFund = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddfund',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addFund();
                    return false;
                } else {
                    return true;
                }
            });
            document.getElementById('fundName').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('fundId');
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
                    deleteFund();
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

addFund = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addfund',
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

loadViewFund = function(viewFundId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/loadviewfund',
        data: {fundId: viewFundId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#updateForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    updateFund();
                    return false;
                } else {
                    return true;
                }
            });             
        //    alert(data.fund.fund_name);
            if(data.fund.fund_id) document.getElementById('fundid').value = data.fund.fund_id;
            if(data.fund.fund_name) document.getElementById('lblfundname').innerHTML = data.fund.fund_name;
            if(data.fund.amount) document.getElementById('lblamount').innerHTML = data.fund.amount;
            if(data.fund.description) document.getElementById('lbldescription').innerHTML = data.fund.description;
        }
    });    
}

editFund = function() {
    var obj = document.getElementById('editButton');
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblfundname').style.display = 'none';
        document.getElementById('lblamount').style.display = 'none';
        document.getElementById('lbldescription').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        
        document.getElementById('saveButton').style.display = '';

        document.getElementById('fundname').style.display = '';
        document.getElementById('fundname').value = document.getElementById('lblfundname').innerHTML;
        document.getElementById('amount').style.display = '';
        document.getElementById('amount').value = document.getElementById('lblamount').innerHTML;
        document.getElementById('description').style.display = '';
        document.getElementById('description').value = document.getElementById('lbldescription').innerHTML;
        
        document.getElementById('fundname').focus();
    } else {
        document.getElementById('lblfundname').style.display = '';
        document.getElementById('lblamount').style.display = '';
        document.getElementById('lbldescription').style.display = '';
        
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        document.getElementById('saveButton').style.display = 'none';
        document.getElementById('fundname').style.display = 'none';
        document.getElementById('amount').style.display = 'none';
        document.getElementById('description').style.display = 'none';
    }
}

updateFund = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updatefund',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            document.getElementById('lblfundname').innerHTML = document.getElementById('fundname').value;
            document.getElementById('lblamount').innerHTML = document.getElementById('amount').value;
            document.getElementById('lbldescription').innerHTML = document.getElementById('description').value;
            editFund();
            successMessage('Ghi lại thành công!');
        }
    });         
}

validateClient = function() {
    closeAllMessage();
    if(document.getElementById('fundName').value == null || document.getElementById('fundName').value.trim().length == 0) {
        showFieldError('fundName','Hãy nhập sổ quỹ');
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
    document.getElementById('fundName').value = '';
    document.getElementById('amount').value = '';
    document.getElementById('description').value = '';
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistfund',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchFund();
                    return false;
                } else {
                    return true;
                }
            });            
            searchFund();
        }
    }); 
}

separateAmount = function(obj) {
    var data = obj.value.replace(new RegExp(',', 'g'), '');    
    data = separate(data);
    obj.value = data.trim();
}

separate = function(text) {
    var count = 0;
    for(var i = text.length - 1 ; i >= 0; i--) {
        count++;
        if(i != 0 && count % 3 === 0) {
            text = text.slice(0, i) + ',' + text.slice(i);
        }
    }
    return text;
}