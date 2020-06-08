searchPlan = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#orderTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchplan',
            data: function ( d ) {
                d.ordercode = $('#ordercode').val();
                d.customer = $('#customer').val();
                d.maid = $('#maid').val();
                d.status = $('#status').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();
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
                    searchPlan();
                    return false;
                } else {
                    return true;
                }
            });
            var today = new Date();
            var dd = today.getDate();
            var mm = today.getMonth()+1; //January is 0!
            var yyyy = today.getFullYear();

            if(dd<10) {
                dd = '0'+dd
            } 

            if(mm<10) {
                mm = '0'+mm
            } 

            today = dd + '-' + mm + '-' + yyyy;
            document.getElementById('todate').value = today;
            
            searchPlan();
            document.getElementById('ordercode').focus();
        }
    }); 
}

viewPlan = function(id) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewplan',
        data: {planid: id},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            document.getElementById('parent').value = parentPage;
            document.getElementById('planId').value = data.plan.plan_id;
            if(data.plan.plan_code) document.getElementById('lblPlanCode').innerHTML = data.plan.plan_code;
            if(data.plan.status) {
                if(data.plan.status == 1) document.getElementById('lblStatus').innerHTML = 'Chưa thực hiện';
                if(data.plan.status == 2) document.getElementById('lblStatus').innerHTML = 'Nhận việc';
                if(data.plan.status == 3) document.getElementById('lblStatus').innerHTML = 'Thay người';
                if(data.plan.status == 4) document.getElementById('lblStatus').innerHTML = 'Đang thực hiện';
                if(data.plan.status == 5) document.getElementById('lblStatus').innerHTML = 'Hoàn thành';
                if(data.plan.status == 6) document.getElementById('lblStatus').innerHTML = 'Đóng';
            }
            if(data.plan.customer_name) document.getElementById('lblCustomerName').innerHTML = data.plan.customer_name;
            
            if(data.plan.maid_name) document.getElementById('lblMaidName').innerHTML = data.plan.maid_name;
           
            if(data.plan.start_date) document.getElementById('lblStart').innerHTML = data.plan.start_date;
            if(data.plan.end_date) document.getElementById('lblEnd').innerHTML = data.plan.end_date;
            if(data.plan.real_start) document.getElementById('lblRealStart').innerHTML = data.plan.real_start;
            if(data.plan.real_end) document.getElementById('lblRealEnd').innerHTML = data.plan.real_end;
            if(data.plan.customer_rating) {
                var rating = data.plan.customer_rating;
                var innerTemp = '';
                for(var i = 0; i < rating; i++) innerTemp += '<span class="fa fa-star"></span>';
                for(var i = rating; i < 5; i++) innerTemp += '<span class="fa fa-star-o"></span>';
                document.getElementById('customerRatingDiv').innerHTML = innerTemp;
            }
            if(data.plan.time_comment) {
                document.getElementById('lblTimeComment').innerHTML = data.plan.time_comment;
            }
            if(data.plan.customer_comment) {
                document.getElementById('lblCustomerComment').innerHTML = data.plan.customer_comment;
            }
            if(data.plan.price) {
                document.getElementById('lblPrice').innerHTML = separate(data.plan.price.toString());
            }            
            
            if(data.plan.rating_customer) {
                var rating = data.plan.rating_customer;
                var innerTemp = '';
                for(var i = 0; i < rating; i++) innerTemp += '<span class="fa fa-star"></span>';
                for(var i = rating; i < 5; i++) innerTemp += '<span class="fa fa-star-o"></span>';
                document.getElementById('ratingCustomerDiv').innerHTML = innerTemp;
            }
            if(data.plan.rating_work) {
                var rating = data.plan.rating_work;
                var innerTemp = '';
                for(var i = 0; i < rating; i++) innerTemp += '<span class="fa fa-star"></span>';
                for(var i = rating; i < 5; i++) innerTemp += '<span class="fa fa-star-o"></span>';                
                document.getElementById('ratingWorkDiv').innerHTML = innerTemp;
            }            
            if(data.plan.time_maid_comment) {
                document.getElementById('lblTimeMaidComment').innerHTML = data.plan.time_maid_comment;
            }            
            if(data.plan.maid_comment) {
                document.getElementById('lblMaidComment').innerHTML = data.plan.maid_comment;
            }
            if(data.plan.salary) {
                document.getElementById('lblSalary').innerHTML = separate(data.plan.salary.toString());
            }

            document.getElementById('customerView').innerHTML = '<img src="/image?filename=' + data.plan.customer_picture + '&time=' + (new Date()).getTime() + '" style="height:135px; border-radius: 3px;"/>';
            
            setTimeout(function () {
                    document.getElementById('maidView').innerHTML = '<img src="/image?filename=' + data.plan.maid_picture + '&time=' + (new Date()).getTime() + '" style="height:135px; border-radius: 3px;"/>';
                }, 100);
                        
            var innerMaid = '';
            var check = '0';
            for(var i = 0; i < data.maid.length; i++) {
                if(data.plan.maid_id == data.maid[i].user_id) check = '1';
                innerMaid += '<option value="' + data.maid[i].user_id + '">' + data.maid[i].name + ' - ' + data.maid[i].mobile + '</option>';
            }
            if(data.plan.maid_id && check == '0') {
                innerMaid += '<option value="' + data.plan.maid_id + '" selected >' + data.plan.maid_name + '</option>';
            }
            document.getElementById('maidId').innerHTML = innerMaid; 

            if(data.plan.maid_id) {
                var cboMaidId = document.getElementById('maidId');
                cboMaidId.value = data.plan.maid_id;
                document.getElementById('lblMaidName').innerHTML = cboMaidId.options[cboMaidId.selectedIndex].innerHTML;
            }
        }
    });    
}

editPlan = function() {
    var obj = document.getElementById('editButton');
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        document.getElementById('lblMaidName').style.display = 'none';
        document.getElementById('lblStart').style.display = 'none';
        document.getElementById('lblEnd').style.display = 'none';
        document.getElementById('lblStatus').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        document.getElementById('saveButton').style.display = '';

        document.getElementById('maidId').style.display = '';
        document.getElementById('start').parentNode.style.display = '';
        document.getElementById('start').value = document.getElementById('lblStart').innerHTML;
        document.getElementById('end').parentNode.style.display = '';
        document.getElementById('end').value = document.getElementById('lblEnd').innerHTML;
        document.getElementById('status').style.display = '';
        
        var cboMaidId = document.getElementById('maidId');
        for(var i = 0; i < cboMaidId.options.length; i++) {
            if(cboMaidId.options[i].innerHTML == document.getElementById('lblMaidId').innerHTML) {
                cboMaidId.value = cboMaidId.options[i].value;
                break;
            }
        }
        cboMaidId.focus();
    } else {
        document.getElementById('lblMaidName').style.display = '';
        document.getElementById('lblStart').style.display = '';
        document.getElementById('lblEnd').style.display = '';
        document.getElementById('lblStatus').style.display = '';
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        document.getElementById('saveButton').style.display = 'none';        
        document.getElementById('maidId').style.display = 'none';
        document.getElementById('start').parentNode.style.display = 'none';
        document.getElementById('end').parentNode.style.display = 'none';
        document.getElementById('status').style.display = 'none';
    }
}

updatePlan = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updateplan',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            var cboMaidId = document.getElementById('maidId');
            document.getElementById('lblMaidName').innerHTML = cboMaidId.options[cboMaidId.selectedIndex].innerHTML;

            editPlan();
            successMessage('Ghi lại thành công!');
        }
    });         
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