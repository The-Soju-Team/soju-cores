searchReceipt = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     

    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/getallfund',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var arrFund = data.fund;
            
            $('#receiptTable').DataTable({
                destroy: true,
                processing: false,
                serverSide: true,
                searching: false,
                bSort: false,
                columnDefs: [
                    {
                        targets: 5,
                        render: function (data, type, full, meta) {
                            if (data) return separate(data.toString());
                            return data;
                        }
                    },                    
                    {
                        targets: 7,
                        render: function (data, type, full, meta) {
                            if (full[9] == 'Đã xác nhận') return data;
                            var innerDiv = '<select name="fund">';
                            for (var i = 0; i < arrFund.length; i++) {
                                if(arrFund[i].fund_name) {
                                    innerDiv += '<option value="' + arrFund[i].fund_id + '_' + full[5] + '"';
                                    if(data && arrFund[i].fund_id == data) innerDiv += 'selected';
                                    innerDiv += '>' + arrFund[i].fund_name + '</option>';
                                }
                            }
                            innerDiv += '</select>';
                            return innerDiv;
                        }
                    },
                    {
                        targets: 10,
                        render: function (data, type, full, meta) {
                            if (full[9] == 'Đã xác nhận')
                                return '';
                            else
                                return data;
                        }
                    }
                ],
                ajax: {
                    type: 'POST',
                    dataType: 'json',
                    url: '/searchreceipt',
                    data: function (d) {
                        d.receiptNo = $('#receipt_no').val();
                        d.payerName = $('#payer_name').val();
                        d.receiverName = $('#receiver_name').val();
                        d.fromdate = $('#fromdate').val();
                        d.todate = $('#todate').val();
                        if (document.getElementById('isaccept').value == '1') {
                            d.isaccept = '1';
                            var arrayInput = $('#receiptTable_wrapper :input[type="checkbox"]');
                            d.receiptId = arrayInput.serialize();
                            var arrayFund = '';
                            for (var i = 0; i < arrayInput.length; i++) {
                                if (arrayInput[i].id != 'selectAll')
                                    arrayFund += ',' + arrayInput[i].parentNode.parentNode.getElementsByTagName('select')[0].value;
                            }
                            d.fund = arrayFund.substring(1);
                        }
                        else
                            d.isaccept = '0';
                        
                        if(document.getElementById('isdelete').value == '1') {
                            d.isdelete = '1';
                            d.userid = $('#userTable_wrapper :input[type="checkbox"]').serialize();
                        }
                        else d.isdelete = '0';                        
                    },
                    cache: false
                },
                initComplete: function (settings, json) {
                    if (document.getElementById('isaccept').value == '1') {
                        successMessage('Xác nhận thành công!');
                    }
                    document.getElementById('isaccept').value = '0';
                    validateCheckAll();
                },
                language: {
                    'decimal': '',
                    'emptyTable': 'Không có dữ liệu',
                    'info': 'Từ _START_ đến _END_ của _TOTAL_ bản ghi',
                    'infoEmpty': 'Từ 0 đến 0 của 0 bản ghi',
                    'infoFiltered': '(Lọc từ _MAX_ tổng số bản ghi)',
                    'infoPostFix': '',
                    'thousands': ',',
                    'lengthMenu': 'Hiển thị _MENU_ dòng/trang',
                    'loadingRecords': 'Đang tải...',
                    'processing': 'Đang xử lý...',
                    'search': 'Tìm kiếm:',
                    'zeroRecords': 'Không tìm thấy dữ liệu',
                    'paginate': {
                        'first': 'Đầu',
                        'last': 'Cuối',
                        'next': 'Tiếp',
                        'previous': 'Trước'
                    },
                    'aria': {
                        'sortAscending': ': Sắp xếp tăng dần',
                        'sortDescending': ': Sắp xếp giảm dần'
                    }
                }
            });
        }
    });    
}

checkAll = function (obj) {
    var arrCheckbox = document.getElementsByName('receiptId');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('receiptId');
    var checkboxAll = document.getElementById('selectAll');
    if(checkboxAll) {
        checkboxAll.checked = true;
        for(var i = 0; i < arrCheckbox.length; i++) {
            if(!arrCheckbox[i].checked) {
                checkboxAll.checked = false;
                break;
            }
        }    
    }
}

viewAddReceipt = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewaddreceipt',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#addForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    addReceipt();
                    return false;
                } else {
                    return true;
                }
            });
            
            var innerFundId = '<option value=""></option>';
            for(var i = 0; i < data.fund.length; i++) {
                innerFundId += '<option value="' + data.fund[i].fund_id + '">' + data.fund[i].fund_name + '</option>';
            }
            document.getElementById('fundId').innerHTML = innerFundId;
            
            var innerUser = '<option value=""></option>';
            for(var i = 0; i < data.user.length; i++) {
                innerUser += '<option value="' + data.user[i].user_id + '">' + data.user[i].name + '</option>';
            }
            document.getElementById('payerId').innerHTML = innerUser;
            document.getElementById('receiverId').innerHTML = innerUser;
            
            $('#payDate').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            document.getElementById('payerName').focus();
        }
    });
}
addReceipt = function() {
      $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/addreceipt',
            data: $("#addForm").serialize(),
            cache: false, //fix loop IE
            success: function(data, textStatus, jqXHR) {
                closeAllMessage();
                successMessage('Thêm mới thành công!');
                clearForm();
            }
        });
}

acceptReceipt = function() {
    document.getElementById('isaccept').value = '1';
    searchReceipt();
}

deleteReceipt = function() {
    document.getElementById('isdelete').value = '1';
    searchReceipt();
}

acceptOnClick = function() {
    var arrCheckbox = document.getElementsByName('receiptId');
    var check = false;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(arrCheckbox[i].checked) {
            check = true;
            break;
        }
    }
    if(check) confirmAccept();
    else errorMessage('Bạn chưa chọn phiếu thu để xác nhận!');
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('receiptId');
    var check = false;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(arrCheckbox[i].checked) {
            check = true;
            break;
        }
    }
    if(check) confirmDelete();
    else errorMessage('Bạn chưa chọn phiếu thu để hủy!');
}

confirmDelete = function() {
    noty({
        text: 'Bạn có muốn hủy phiếu thu không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    deleteReceipt();
                }},
                {addClass: 'btn btn-info btn-clean', text: 'Hủy bỏ', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

confirmAccept = function() {
    noty({
        text: 'Bạn có muốn xác nhận phiếu thu không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    acceptReceipt();
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
    $('#addForm input').val('');
}

function loadScript(src,callback){
    var script = document.createElement("script");
    script.type = "text/javascript";
    if(callback)script.onload=callback;
    document.getElementsByTagName("head")[0].appendChild(script);
    script.src = src;
}

backToList = function() {
    var parentPage = '';
    if(document.getElementById('parent')) parentPage = document.getElementById('parent').value;
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/backlistuser',
        data: {parent: parentPage},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            $('#searchForm').keypress(function (e) {
                if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
                    searchReceipt();
                    return false;
                } else {
                    return true;
                }
            });
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth() + 1; //January is 0!
            var yyyy = today.getFullYear();

            if (dd < 10) {
                dd = '0' + dd
            }

            if (mm < 10) {
                mm = '0' + mm
            }

            today = dd + '-' + mm + '-' + yyyy;

            document.getElementById('todate').value = today;
            searchReceipt();
        }
    }); 
}

changePayer = function() {
    if(document.getElementById('divPayerName').style.display == 'none') {
        document.getElementById('divPayerName').style.display = '';
        document.getElementById('divPayerMobile').style.display = '';
        document.getElementById('lblPayerMobile').style.display = '';
        document.getElementById('divPayerId').style.display = 'none';
        document.getElementById('divPayerButton').style.display = 'none';        
        document.getElementById('payer').value = '';
        document.getElementById('payerId').value = '';
    } else {
        document.getElementById('divPayerName').style.display = 'none';
        document.getElementById('divPayerMobile').style.display = 'none';
        document.getElementById('lblPayerMobile').style.display = 'none';
        document.getElementById('payerName').value = '';
        document.getElementById('payerMobile').value = '';
        document.getElementById('divPayerId').style.display = '';
        document.getElementById('divPayerButton').style.display = '';
    }
}

changeReceiver = function() {
    if(document.getElementById('divReceiverName').style.display == 'none') {
        document.getElementById('divReceiverName').style.display = '';
        document.getElementById('divReceiverMobile').style.display = '';
        document.getElementById('lblReceiverMobile').style.display = '';
        document.getElementById('divReceiverId').style.display = 'none';
        document.getElementById('divReceiverButton').style.display = 'none';        
        document.getElementById('receiver').value = '';
        document.getElementById('receiverId').value = '';
    } else {
        document.getElementById('divReceiverName').style.display = 'none';
        document.getElementById('divReceiverMobile').style.display = 'none';
        document.getElementById('lblReceiverMobile').style.display = 'none';
        document.getElementById('receiverName').value = '';
        document.getElementById('receiverMobile').value = '';
        document.getElementById('divReceiverId').style.display = '';
        document.getElementById('divReceiverButton').style.display = '';
    }
}

openPopupUser = function(obj) {
    document.getElementById('userType').value = obj;
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

chooseUser = function(obj) {
    if(document.getElementById('userType').value == 'receiver') {
        document.getElementById('receiverId').value = obj.value;
        var userName = obj.parentNode.parentNode.getElementsByTagName('td')[1];
        document.getElementById('receiver').value = userName.innerHTML;
    } else {
        document.getElementById('payerId').value = obj.value;
        var userName = obj.parentNode.parentNode.getElementsByTagName('td')[1];
        document.getElementById('payer').value = userName.innerHTML;        
    }
    $('#selectModal').modal('hide');
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