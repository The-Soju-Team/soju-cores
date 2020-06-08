searchSalary = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#orderTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,
        columnDefs: [ 
            {
                targets: 3,
                render: function (data, type, full, meta) {
                    if (data) {
                        var salary = separate(data.toString());
                        salary = '<a "javascript:void(0)" data-toggle="modal" data-target="#selectModal" onclick="viewSalary(\'' + full[9] + '\', ' + full[7] + ', ' + full[8]  + ', \'' + full[6] + '\');">' + salary + '</a>';
                        return salary;
                    }
                    return data;
                }
            },             
            {
                targets: 7,
                render: function (data, type, full, meta ) {
                    if(data) {
                        if(full[5]) return null;
                        else {
                            var dataValue = '<input type="checkbox" name="userid" onclick="validateCheckAll()" value="' + data + '_' + full[3] + '_' + full[8] +'"/>';
                            return dataValue;
                        }
                    } else return data;
                }
            }            
        ],        
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchsalary',
            data: function ( d ) {
                d.maid = $('#maid').val();
                d.mobile = $('#mobile').val();
                d.status = $('#status').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();
                if(document.getElementById('ispay').value == '1') {
                    d.ispay = '1';
                    d.userid = $('#orderTable_wrapper :input[type="checkbox"]').serialize();
                }
                else d.ispay = '0';                
            },
            cache: false
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

viewSalary = function(userName, userId, salaryLevel, spendId) { 
    document.getElementById('lblUserName').innerHTML = userName + ' (hệ số: ' + salaryLevel + ')';
    var sumSalary = 0;
    var sumReward = 0;
    var sumPenalty = 0;
    $('#planTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,
        bPaginate: false,
        columnDefs: [ 
            {
                targets: 5,
                render: function (data, type, full, meta) {
                    sumSalary += data;
                    sumReward += full[6];
                    sumPenalty += full[7];
                    return data;
                }
            }
        ],
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/viewsalary',
            data: function ( d ) {
                d.maidId = userId;
                d.spendId = spendId;
            },
            cache: false
        },
        initComplete: function(settings, json) {
            document.getElementById('planTable_info').style.display = 'none';
            document.getElementById('sumSalary').innerHTML = sumSalary/2;
            document.getElementById('sumReward').innerHTML = sumReward/2;
            document.getElementById('sumPenalty').innerHTML = sumPenalty/2;
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
                    searchSalary();
                    return false;
                } else {
                    return true;
                }
            });
            searchSalary();
            document.getElementById('maid').focus();
        }
    }); 
}

payOnClick = function() {
    var arrCheckbox = document.getElementsByName('userid');
    var check = false;
    for(var i = 0; i < arrCheckbox.length; i++) {
        if(arrCheckbox[i].checked) {
            check = true;
            break;
        }
    }
    if(check) confirmPay();
    else errorMessage('Bạn chưa chọn người để trả lương!');
}

confirmPay = function() {
    noty({
        text: 'Bạn có muốn trả lương không?',
        layout: 'center',
        type: 'info',
        buttons: [
                {addClass: 'btn btn-info btn-clean', text: 'Đồng ý', onClick: function($noty) {
                    $noty.close();
                    paySalary();
                }},
                {addClass: 'btn btn-info btn-clean', text: 'Hủy bỏ', onClick: function($noty) {
                    $noty.close();
                }}
            ]
    });    
}

paySalary = function() {
    document.getElementById('ispay').value = '1';
    searchSalary();
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