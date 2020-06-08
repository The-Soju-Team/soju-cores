searchPolicy = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#policyTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchpolicy',
            data: function ( d ) {
                d.policyCode = $('#policyCode').val();
                d.content = $('#content').val();
                d.time = $('#time').val();
                d.status = $('#status').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();      
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.policyId = $('#policyTable_wrapper :input[type="checkbox"]').serialize();
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
    var arrCheckbox = document.getElementsByName('policyId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('policyId');
    var checkboxAll = document.getElementById('selectAll')
    checkboxAll.checked = true;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(!arrCheckbox[i].checked) {
            checkboxAll.checked = false;
            break;
        }
    }    
}

deletePolicy = function() {
    document.getElementById('isdelete').value = '1';
    searchPolicy();
}

viewAddPolicy = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddpolicy',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#fromDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#toDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addPolicy();
                    return false;
                } else {
                    return true;
                }
            });
            var innerArea = '<option value="0"></option>';
            for(var i = 0; i < data.areas.length; i++) {
                innerArea += '<option value="' + data.areas[i].area_id + '">' + data.areas[i].area_name + '</option>';
            }
            document.getElementById('areaId').innerHTML = innerArea;
            document.getElementById('policyCode').focus();
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('policyId');
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
                    deletePolicy();
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

addPolicy = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addpolicy',
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
    if(document.getElementById('policyCode').value == null || document.getElementById('policyCode').value.trim().length == 0) {
        showFieldError('policyCode','Hãy nhập Mã chính sách');
        return false;
    } else if(document.getElementById('fromDate').value == null || document.getElementById('fromDate').value.trim().length == 0) {
        showFieldError('fromDate','Hãy nhập Áp dụng từ ngày');
        return false;
    } else if(document.getElementById('toDate').value == null || document.getElementById('toDate').value.trim().length == 0) {
        showFieldError('toDate','Hãy nhập Áp dụng đến ngày');
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
    document.getElementById('policyCode').value = '';
    document.getElementById('content').value = '';
    document.getElementById('timeFrom').value = '';
    document.getElementById('timeTo').value = '';
    document.getElementById('fromDate').value = '';
    document.getElementById('toDate').value = '';
    document.getElementById('areaId').value = '';
    document.getElementById('status').value = '';
    document.getElementById('addRate').value = '';
    document.getElementById('addVnd').value = '';
}

viewPolicy = function(viewPolicyId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewpolicy',
        data: {policyId: viewPolicyId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#fromDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#toDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            $('#updateForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    updatePolicy();
                    return false;
                } else {
                    return true;
                }
            });
            var innerArea = '<option value="0"></option>';
            for(var i = 0; i < data.areas.length; i++) {
                innerArea += '<option value="' + data.areas[i].area_id + '">' + data.areas[i].area_name + '</option>';
            }
            document.getElementById('areaId').innerHTML = innerArea;            
            if(data.policy.policy_id) document.getElementById('policyId').value = data.policy.policy_id;
            if(data.policy.policy_code) document.getElementById('lblPolicyCode').innerHTML = data.policy.policy_code;
            if(data.policy.content) document.getElementById('lblContent').innerHTML = data.policy.content;
            if(data.policy.time_from || data.policy.time_from === 0) document.getElementById('lblTimeFrom').innerHTML = data.policy.time_from;
            if(data.policy.time_to || data.policy.time_to === 0) document.getElementById('lblTimeTo').innerHTML = data.policy.time_to;
            if(data.policy.from_date) document.getElementById('lblFromDate').innerHTML = data.policy.from_date;
            if(data.policy.to_date) document.getElementById('lblToDate').innerHTML = data.policy.to_date;
            if(data.policy.create_date) document.getElementById('lblCreateDate').innerHTML = data.policy.create_date;
            if(data.policy.add_rate) document.getElementById('lblAddRate').innerHTML = data.policy.add_rate;
            if(data.policy.add_vnd) document.getElementById('lblAddVnd').innerHTML = data.policy.add_vnd;
            if(data.policy.status == '1') document.getElementById('lblStatus').innerHTML = 'Chưa áp dụng';
            else if(data.policy.status == '2') document.getElementById('lblStatus').innerHTML = 'Đang áp dụng';
            else if(data.policy.status == '3') document.getElementById('lblStatus').innerHTML = 'Hết hiệu lực';
            if(data.policy.area_id) {
                var cboAreaId = document.getElementById('areaId');
                cboAreaId.value = data.policy.area_id;
                document.getElementById('lblAreaId').innerHTML = cboAreaId.options[cboAreaId.selectedIndex].innerHTML;
            }
        }
    });    
}

editPolicy = function() {
    var obj = document.getElementById('editButton');
    var divFrom = document.getElementById('fromDate').parentNode;
    var divTo = document.getElementById('toDate').parentNode;

    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblPolicyCode').style.display = 'none';
        document.getElementById('lblContent').style.display = 'none';
        document.getElementById('lblTimeFrom').style.display = 'none';
        document.getElementById('lblTimeTo').style.display = 'none';
        document.getElementById('lblFromDate').style.display = 'none';
        document.getElementById('lblToDate').style.display = 'none';
        document.getElementById('lblStatus').style.display = 'none';
        document.getElementById('lblAreaId').style.display = 'none';
        document.getElementById('lblAddRate').style.display = 'none';
        document.getElementById('lblAddVnd').style.display = 'none';
        
        document.getElementById('divCreateDate').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        document.getElementById('saveButton').style.display = '';

        document.getElementById('policyCode').style.display = '';
        document.getElementById('policyCode').value = document.getElementById('lblPolicyCode').innerHTML;
        document.getElementById('content').style.display = '';
        document.getElementById('content').value = document.getElementById('lblContent').innerHTML;
        document.getElementById('timeFrom').style.display = '';
        document.getElementById('timeFrom').value = document.getElementById('lblTimeFrom').innerHTML;
        document.getElementById('timeTo').style.display = '';
        document.getElementById('timeTo').value = document.getElementById('lblTimeTo').innerHTML;
        divFrom.style.display = '';
        document.getElementById('fromDate').value = document.getElementById('lblFromDate').innerHTML;
        divTo.style.display = '';
        document.getElementById('toDate').value = document.getElementById('lblToDate').innerHTML;
        document.getElementById('addRate').style.display = '';
        document.getElementById('addRate').value = document.getElementById('lblAddRate').innerHTML;
        document.getElementById('addVnd').style.display = '';
        document.getElementById('addVnd').value = document.getElementById('lblAddVnd').innerHTML;        
        document.getElementById('areaId').style.display = '';
        document.getElementById('status').style.display = '';
        if(document.getElementById('lblStatus').innerHTML == 'Chưa áp dụng') document.getElementById('status').value = '1';
        else if(document.getElementById('lblStatus').innerHTML == 'Đang áp dụng') document.getElementById('status').value = '2';
        else if(document.getElementById('lblStatus').innerHTML == 'Hết hiệu lực') document.getElementById('status').value = '3';
        
        document.getElementById('policyCode').focus();
    } else {
        document.getElementById('lblPolicyCode').style.display = '';
        document.getElementById('lblContent').style.display = '';
        document.getElementById('lblTimeFrom').style.display = '';
        document.getElementById('lblTimeTo').style.display = '';
        document.getElementById('lblFromDate').style.display = '';
        document.getElementById('lblToDate').style.display = '';
        document.getElementById('lblStatus').style.display = '';
        document.getElementById('lblAreaId').style.display = '';
        document.getElementById('divCreateDate').style.display = '';
        document.getElementById('lblAddRate').style.display = '';
        document.getElementById('lblAddVnd').style.display = '';        
        
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        document.getElementById('saveButton').style.display = 'none';
        document.getElementById('policyCode').style.display = 'none';
        document.getElementById('content').style.display = 'none';
        document.getElementById('timeFrom').style.display = 'none';
        document.getElementById('timeTo').style.display = 'none';
        divFrom.style.display = 'none';
        divTo.style.display = 'none';
        document.getElementById('status').style.display = 'none';
        document.getElementById('areaId').style.display = 'none';
        document.getElementById('addRate').style.display = 'none';
        document.getElementById('addVnd').style.display = 'none';
    }
}

updatePolicy = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updatepolicy',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            document.getElementById('lblPolicyCode').innerHTML = document.getElementById('policyCode').value;
            document.getElementById('lblContent').innerHTML = document.getElementById('content').value;
            document.getElementById('lblTimeFrom').innerHTML = document.getElementById('timeFrom').value;
            document.getElementById('lblTimeTo').innerHTML = document.getElementById('timeTo').value;
            document.getElementById('lblFromDate').innerHTML = document.getElementById('fromDate').value;
            document.getElementById('lblToDate').innerHTML = document.getElementById('toDate').value;
            document.getElementById('lblAddRate').innerHTML = document.getElementById('addRate').value;
            document.getElementById('lblAddVnd').innerHTML = document.getElementById('addVnd').value;            
            
            if(document.getElementById('status').value == '1') document.getElementById('lblStatus').innerHTML = 'Chưa áp dụng';
            else if(document.getElementById('status').value == '2') document.getElementById('lblStatus').innerHTML = 'Đang áp dụng';
            else if(document.getElementById('status').value == '3') document.getElementById('lblStatus').innerHTML = 'Hết hiệu lực';

            var cboAreaId = document.getElementById('areaId');
            document.getElementById('lblAreaId').innerHTML = cboAreaId.options[cboAreaId.selectedIndex].innerHTML;

            editPolicy();
            successMessage('Ghi lại thành công!');
        }
    });         
}

backToList = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistpolicy',
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchPolicy();
                    return false;
                } else {
                    return true;
                }
            });            
            searchPolicy();
        }
    }); 
}
