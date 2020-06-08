searchUser = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#userTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchuser',
            data: function ( d ) {
                d.username = $('#username').val();
                d.mobile = $('#mobile').val();
                d.home = $('#home').val();
                d.usertype = $('#usertype').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();      
                if(document.getElementById('isdelete').value == '1') {
                    d.isdelete = '1';
                    d.userid = $('#userTable_wrapper :input[type="checkbox"]').serialize();
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

searchPopup = function() {
    $('#fromdate').datepicker({pickTime: false, format: "dd-mm-yyyy" });
    $('#todate').datepicker({pickTime: false, format: "dd-mm-yyyy" });     
    $('#userTable').DataTable( {
        destroy: true,
        processing: false,
        serverSide: true,
        searching: false,    
        bSort: false,       
        ajax: {
            type: 'POST',
            dataType: 'json',
            url: '/searchpopup',
            data: function ( d ) {
                d.username = $('#username').val();
                d.mobile = $('#mobile').val();
                d.home = $('#home').val();
                d.usertype = $('#usertype').val();      
                d.fromdate = $('#fromdate').val();
                d.todate = $('#todate').val();      
            },
            cache: false
        },
        initComplete: function(settings, json) {
            document.getElementById('username').focus();
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
    var arrCheckbox = document.getElementsByName('userid');
    for(var i = 0; i < arrCheckbox.length; i++) {
        arrCheckbox[i].checked = obj.checked;
    }
}

validateCheckAll = function() {
    var arrCheckbox = document.getElementsByName('userid');
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

deleteUser = function() {
    document.getElementById('isdelete').value = '1';
    searchUser();
}

viewAddUser = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/viewadduser',
        data: {},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            configDropzone();
            $('#birthday').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            document.getElementById('username').focus();
            document.getElementById('latitude').value = 21.0320057;
            document.getElementById('longitude').value = 105.7722561;

            loadScript('https://maps.googleapis.com/maps/api/js?key=AIzaSyD18jCVXG3nu0ki_CCxmXYBszepbOZSYok&libraries=places&callback=initMap');            
        }
    });
}

deleteOnClick = function() {
    var arrCheckbox = document.getElementsByName('userid');
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
                    deleteUser();
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

addUser = function() {
    if(validateClient()) {
        $.ajax({ 
            type: 'POST',
            dataType: 'json',
            url: '/adduser',
            data: $("#addForm").serialize(),
            cache: false, //fix loop IE
            success: function(data, textStatus, jqXHR) {
                closeAllMessage();
                if(data.error_code == 'createuser_04') {
                    errorMessage("Số điện thoại đã được đăng ký!");
                } else {
                    successMessage('Thêm mới thành công!');
                    clearForm();
                }
            }
        });
    }
}

validateClient = function() {
    closeAllMessage();
    if(document.getElementById('username').value == null || document.getElementById('username').value.trim().length == 0) {
        showFieldError('username','Hãy nhập Họ tên');
        return false;
    } else if(document.getElementById('mobile').value == null || document.getElementById('mobile').value.trim().length == 0) {
        showFieldError('mobile','Hãy nhập Số điện thoại');
        return false;
    } else if(document.getElementById('mobile').value.trim().length >= 15) {
        showFieldError('mobile','Số điện thoại phải ít hơn 15 ký tự');
        return false;
    } else if(!(/^\d+$/.test(document.getElementById('mobile').value.trim()))) {
        showFieldError('mobile','Số điện thoại phải là kiểu số và ít hơn 15 ký tự');
        return false;
    } else if(document.getElementById('password').value == null || document.getElementById('password').value.trim().length == 0) {
        showFieldError('password','Hãy nhập Mật khẩu');
        return false;
    } 
    return true;
}

var myDropzone;
configDropzone = function() {
    Dropzone.autoDiscover = false;
    myDropzone = new Dropzone('#my-awesome-dropzone',{ 
        url: '/uploaduserpic', 
        method: 'post',
        paramName: 'file', // The name that will be used to transfer the file
        maxFilesize: 0.5, // MB
        acceptedFiles: 'image/*',
        maxFiles: 1,
        dictInvalidFileType: 'Bạn hãy chọn đúng định dạng file ảnh!',
        dictMaxFilesExceeded: 'Bạn hãy chọn tối đa 1 ảnh!',
        dictFileTooBig: 'Kích thước file tối đa chỉ 500KB!',
        init: function() {
            this.on('error', function(file, errorMessage, third) {
                if (errorMessage === 'Bạn hãy chọn đúng định dạng file ảnh!') {
                    document.getElementById('errorMessage').innerHTML = 'Bạn hãy chọn đúng định dạng file ảnh!';
                    document.getElementById('errorMessage').style.display = '';
                    this.removeFile(file);
                }
                if (errorMessage === 'Bạn hãy chọn tối đa 1 ảnh!') {
                    document.getElementById('errorMessage').innerHTML = 'Bạn hãy chọn tối đa 1 ảnh!';
                    document.getElementById('errorMessage').style.display = '';
                    this.removeFile(file);
                }
                if (errorMessage === 'Kích thước file tối đa chỉ 500KB!') {
                    document.getElementById('errorMessage').innerHTML = 'Kích thước file tối đa chỉ 500KB!';
                    document.getElementById('errorMessage').style.display = '';
                    this.removeFile(file);
                }
            });
        },                                
        accept: function(file, done) {
            document.getElementById('errorMessage').innerHTML = '';
            document.getElementById('errorMessage').style.display = 'none';
            
            if (this.files.length) {
                var _i, _len;
                for (_i = 0, _len = this.files.length; _i < _len - 1; _i++) // -1 to exclude current file
                {
                    if(this.files[_i].name === file.name && this.files[_i].size === file.size && this.files[_i].lastModifiedDate.toString() === file.lastModifiedDate.toString())
                    {
                        document.getElementById('errorMessage').innerHTML = 'File này đã được tải lên server rồi!';
                        document.getElementById('errorMessage').style.display = '';                        
                        this.removeFile(file);
                    }
                }
            }
            done();
        },
        success: function(file, response){
            var data = JSON.parse(response);
            document.getElementById('imagepath').value = data.serverFileName;
            setRemoveFunction(data.localFileName,data.serverFileName);
            console.log(response);
        }
    });    
}

var myFilezone;
configDropzoneFile = function() {
    Dropzone.autoDiscover = false;
    myFilezone = new Dropzone('#fileForm',{ 
        url: '/uploaduserfile', 
        method: 'post',
        paramName: 'file', // The name that will be used to transfer the file
        maxFilesize: 5, // MB
        maxFiles: 1,
        dictMaxFilesExceeded: 'Bạn hãy chọn tối đa 1 ảnh!',
        dictFileTooBig: 'Kích thước file tối đa chỉ 500KB!',
        init: function() {
            this.on('error', function(file, errorFile, third) {
                if (errorFile === 'Bạn hãy chọn tối đa 1 file!') {
                    document.getElementById('errorFile').innerHTML = 'Bạn hãy chọn tối đa 1 ảnh!';
                    document.getElementById('errorFile').style.display = '';
                    this.removeFile(file);
                }
                if (errorFile === 'Kích thước file tối đa chỉ 5MB!') {
                    document.getElementById('errorFile').innerHTML = 'Kích thước file tối đa chỉ 500KB!';
                    document.getElementById('errorFile').style.display = '';
                    this.removeFile(file);
                }
            });
        },                                
        accept: function(file, done) {
            document.getElementById('errorFile').innerHTML = '';
            document.getElementById('errorFile').style.display = 'none';
            
            if (this.files.length) {
                var _i, _len;
                for (_i = 0, _len = this.files.length; _i < _len - 1; _i++) // -1 to exclude current file
                {
                    if(this.files[_i].name === file.name && this.files[_i].size === file.size && this.files[_i].lastModifiedDate.toString() === file.lastModifiedDate.toString())
                    {
                        document.getElementById('errorFile').innerHTML = 'File này đã được tải lên server rồi!';
                        document.getElementById('errorFile').style.display = '';                        
                        this.removeFile(file);
                    }
                }
            }
            done();
        },
        success: function(file, response){
            var data = JSON.parse(response);
            document.getElementById('filepath').value = data.serverFileName;
            setRemoveFunction(data.localFileName,data.serverFileName);
            console.log(response);
        }
    });    
}

setRemoveFunction = function(localFileName, serverFileName) {
    var arrDivName = document.getElementsByClassName('dz-filename');
    for(var i = 0; i < arrDivName.length; i++) {
        if(arrDivName[i].getElementsByTagName('span')[0].innerHTML === localFileName) {
            var removeLink = arrDivName[i].parentNode.parentNode.getElementsByClassName('dz-remove')[0];
            removeLink.id = serverFileName;
            removeLink.onclick = function(obj){
                $.ajax({ 
                    type: 'POST',
                    dataType: 'json',
                    url: '/removefile?fileName=' + obj.srcElement.id,
                    data: {},
                    cache: false
                });                
            };
            break;
        }
    }
}

removeFile = function(serverFileName){
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/removefile?fileName=' + serverFileName,
        data: {},
        cache: false
    });                
};

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
    document.getElementById('username').value = '';
    document.getElementById('mobile').value = '';
    document.getElementById('birthday').value = '';
    document.getElementById('password').value = '';
    document.getElementById('idnumber').value = '';
    document.getElementById('usertype').value = '4';
    document.getElementById('familymobile').value = '';
    document.getElementById('detail').value = '';
    document.getElementById('videourl').value = '';
    document.getElementById('home').value = '';
    document.getElementById('imagepath').value = '';
    myDropzone.removeAllFiles();
}

loadViewUser = function(viewUserId) {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/loadviewuser',
        data: {userid: viewUserId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            var parentPage = document.getElementById('parent').value;
            document.getElementById('content_ajax_wrap').innerHTML = data.page;
            document.getElementById('parent').value = parentPage;
            configDropzone();
            configDropzoneFile();
            $('#birthday').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            
            if(parentPage == 'listUser' || parentPage == 'listMaid' || parentPage == 'listCustomer') {
                document.getElementById('editButton').style.display = '';
            } else {
                document.getElementById('pac-input').style.display = 'none';
            }
            if(data.user.user_id) document.getElementById('userid').value = data.user.user_id;
            if(data.user.name) document.getElementById('lblusername').innerHTML = data.user.name;
            if(data.user.mobile) document.getElementById('lblmobile').innerHTML = data.user.mobile;
            if(data.user.birthday) document.getElementById('lblbirthday').innerHTML = data.user.birthday;
            if(data.user.id_number) document.getElementById('lblidnumber').innerHTML = data.user.id_number;
            if(data.user.family_mobile) document.getElementById('lblfamilyMobile').innerHTML = data.user.family_mobile;
            if(data.user.video_url) document.getElementById('lblvideourl').innerHTML = data.user.video_url;
            if(data.user.salary_level) document.getElementById('lblsalarylevel').innerHTML = data.user.salary_level;
            if(data.user.amount) document.getElementById('lblamount').innerHTML = separate(data.user.amount);
            if(data.user.detail) document.getElementById('lbldetail').innerHTML = data.user.detail;
            if(data.user.home_province) document.getElementById('lblhome').innerHTML = data.user.home_province;
            if(data.user.user_type == '1') document.getElementById('lblusertype').innerHTML = 'Quản trị';
            else if(data.user.user_type == '2') document.getElementById('lblusertype').innerHTML = 'Điều hành';
            else if(data.user.user_type == '3') {
                document.getElementById('lblusertype').innerHTML = 'Giúp việc';
                document.getElementById('maidInfo').style.display = '';
                document.getElementById('lbLevel').style.display = '';
                document.getElementById('divLevel').style.display = '';
                if(data.user.rating) document.getElementById('lblrating').innerHTML = data.user.rating;
                if(data.user.hour_work) document.getElementById('lblhour').innerHTML = data.user.hour_work;
                if(data.user.salary) document.getElementById('lblsalary').innerHTML = data.user.salary;
                if(data.user.reward) document.getElementById('lblreward').innerHTML = data.user.reward;
                if(data.user.penalty) document.getElementById('lblpenalty').innerHTML = data.user.penalty;
            }
            else if(data.user.user_type == '4') {
                document.getElementById('lbAmount').style.display = '';
                document.getElementById('divAmount').style.display = '';
                document.getElementById('lblusertype').innerHTML = 'Khách hàng';
            }
            document.getElementById('picView').innerHTML = '<img src="/image?filename=' + data.user.picture + '" style="height:160px; border-radius: 3px;"/>';
            if(data.user.user_file) document.getElementById('fileView').innerHTML = '<a href="/userfile?filename=' + data.user.user_file + '" class="btn btn-info"><span class="fa fa-download"></span> Tải xuống </a>';
            if(data.user.address) if(document.getElementById('lbladdress')) document.getElementById('lbladdress').innerHTML = data.user.address;
            if(document.getElementById('latitude') && document.getElementById('longitude')){
                document.getElementById('latitude').value = data.user.latitude;
                document.getElementById('longitude').value = data.user.longitude;
            }
            loadScript('https://maps.googleapis.com/maps/api/js?key=AIzaSyD18jCVXG3nu0ki_CCxmXYBszepbOZSYok&libraries=places&callback=initMap');
        }
    });    
}

getMyInfo = function(viewUserId) {
    configDropzone();
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/getmyinfo',
        data: {userid: viewUserId},
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            $('#birthday').datepicker({pickTime: true, format: "dd-mm-yyyy" });
            if(data.user.user_id) document.getElementById('userid').value = data.user.user_id;
            if(data.user.name) document.getElementById('lblusername').innerHTML = data.user.name;
            if(data.user.mobile) document.getElementById('lblmobile').innerHTML = data.user.mobile;
            if(data.user.birthday) document.getElementById('lblbirthday').innerHTML = data.user.birthday;
            if(data.user.id_number) document.getElementById('lblidnumber').innerHTML = data.user.id_number;
            if(data.user.family_mobile) document.getElementById('lblfamilyMobile').innerHTML = data.user.family_mobile;
            if(data.user.video_url) document.getElementById('lblvideourl').innerHTML = data.user.video_url;
            if(data.user.detail) document.getElementById('lbldetail').innerHTML = data.user.detail;
            if(data.user.home_province) document.getElementById('lblhome').innerHTML = data.user.home_province;
            document.getElementById('picView').innerHTML = '<img src="/image?filename=' + data.user.picture + '" style="height:160px; border-radius: 3px;"/>';
            if(data.user.address) if(document.getElementById('lbladdress')) document.getElementById('lbladdress').innerHTML = data.user.address;
            if(document.getElementById('latitude') && document.getElementById('longitude')){
                document.getElementById('latitude').value = data.user.latitude;
                document.getElementById('longitude').value = data.user.longitude;
            }
            loadScript('https://maps.googleapis.com/maps/api/js?key=AIzaSyD18jCVXG3nu0ki_CCxmXYBszepbOZSYok&libraries=places&callback=initMap');
        }
    });    
}

function loadScript(src,callback){
    var script = document.createElement("script");
    script.type = "text/javascript";
    if(callback)script.onload=callback;
    document.getElementsByTagName("head")[0].appendChild(script);
    script.src = src;
}

function initMap() {
    var uluru;
    if(document.getElementById('latitude').value && document.getElementById('longitude').value && document.getElementById('latitude').value != "undefined" && document.getElementById('longitude').value != "undefined")
        uluru = {lat: parseFloat(document.getElementById('latitude').value), lng: parseFloat(document.getElementById('longitude').value)};
    else {
        uluru = {lat: 21.0320057, lng: 105.7722561};
        document.getElementById('latitude').value = '';
        document.getElementById('longitude').value = '';
    }
    //---------------------------------------------------------------
    var map = new google.maps.Map(document.getElementById('map'), {
        center: uluru,
        zoom: 16
    });

    // Create the search box and link it to the UI element.
    var input = document.getElementById('pac-input');
    var searchBox = new google.maps.places.SearchBox(input);
    map.controls[google.maps.ControlPosition.TOP_LEFT].push(input);

    // Bias the SearchBox results towards current map's viewport.
    map.addListener('bounds_changed', function () {
        searchBox.setBounds(map.getBounds());
    });

    // Listen for the event fired when the user selects a prediction and retrieve
    // more details for that place.
    searchBox.addListener('places_changed', function () {
        var places = searchBox.getPlaces();

        if (places.length == 0) {
            return;
        }

        // For each place, get the icon, name and location.
        var bounds = new google.maps.LatLngBounds();
        places.forEach(function (place) {
            if (!place.geometry) {
                return;
            }

            if (place.geometry.viewport) {
                // Only geocodes have viewport.
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }
        });
        map.fitBounds(bounds);
    });
    
    //---------------------------------------------------------------
    var marker = new google.maps.Marker({
        position: uluru,
        map: map
    });
    
    google.maps.event.addListener(map, 'click', function(event) {
        placeMarker(event.latLng);
    });

    function placeMarker(position) {
        marker.setPosition(position);
        document.getElementById('latitude').value = position.lat();
        document.getElementById('longitude').value = position.lng();
        //map.setCenter(location);
    }    
}

editUser = function() {
    var obj = document.getElementById('editButton');
    var divBirth = document.getElementById('birthday').parentNode;
    if(obj.innerHTML == '<span class="fa fa-pencil"></span> Sửa') {
        if(document.getElementById('lblusername'))document.getElementById('lblusername').style.display = 'none';
        if(document.getElementById('lblmobile'))document.getElementById('lblmobile').style.display = 'none';
        if(document.getElementById('lblbirthday'))document.getElementById('lblbirthday').style.display = 'none';
        if(document.getElementById('lblidnumber'))document.getElementById('lblidnumber').style.display = 'none';
        if(document.getElementById('lblfamilyMobile'))document.getElementById('lblfamilyMobile').style.display = 'none';
        if(document.getElementById('lblvideourl'))document.getElementById('lblvideourl').style.display = 'none';
        if(document.getElementById('lblsalarylevel'))document.getElementById('lblsalarylevel').style.display = 'none';
        if(document.getElementById('lblamount'))document.getElementById('lblamount').style.display = 'none';
        if(document.getElementById('lblhome'))document.getElementById('lblhome').style.display = 'none';
        if(document.getElementById('lbldetail'))document.getElementById('lbldetail').style.display = 'none';
        if(document.getElementById('lblpassword'))document.getElementById('lblpassword').style.display = 'none';
        if(document.getElementById('picView'))document.getElementById('picView').style.display = 'none';
        if(document.getElementById('fileView'))document.getElementById('fileView').style.display = 'none';
        if(document.getElementById('lblusertype'))document.getElementById('lblusertype').style.display = 'none';
        if(document.getElementById('lbladdress'))document.getElementById('lbladdress').style.display = 'none';
        obj.innerHTML = '<span class="fa fa-close"></span> Hủy bỏ';
        if(document.getElementById('saveButton'))document.getElementById('saveButton').style.display = '';

        if(document.getElementById('username'))document.getElementById('username').style.display = '';
        if(document.getElementById('username'))document.getElementById('username').value = document.getElementById('lblusername').innerHTML;
        if(document.getElementById('mobile'))document.getElementById('mobile').style.display = '';
        if(document.getElementById('mobile'))document.getElementById('mobile').value = document.getElementById('lblmobile').innerHTML;
        divBirth.style.display = '';
        if(document.getElementById('birthday'))document.getElementById('birthday').value = document.getElementById('lblbirthday').innerHTML;
        if(document.getElementById('idnumber'))document.getElementById('idnumber').style.display = '';
        if(document.getElementById('idnumber'))document.getElementById('idnumber').value = document.getElementById('lblidnumber').innerHTML;
        if(document.getElementById('familymobile'))document.getElementById('familymobile').style.display = '';
        if(document.getElementById('familymobile'))document.getElementById('familymobile').value = document.getElementById('lblfamilyMobile').innerHTML;
        if(document.getElementById('detail'))document.getElementById('detail').style.display = '';
        if(document.getElementById('detail'))document.getElementById('detail').value = document.getElementById('lbldetail').innerHTML;
        if(document.getElementById('videourl'))document.getElementById('videourl').style.display = '';
        if(document.getElementById('videourl'))document.getElementById('videourl').value = document.getElementById('lblvideourl').innerHTML;        
        if(document.getElementById('salarylevel'))document.getElementById('salarylevel').style.display = '';
        if(document.getElementById('salarylevel'))document.getElementById('salarylevel').value = document.getElementById('lblsalarylevel').innerHTML;
        if(document.getElementById('amount'))document.getElementById('amount').style.display = '';
        if(document.getElementById('amount'))document.getElementById('amount').value = document.getElementById('lblamount').innerHTML;
        if(document.getElementById('home'))document.getElementById('home').style.display = '';
        if(document.getElementById('home'))document.getElementById('home').value = document.getElementById('lblhome').innerHTML;
        if(document.getElementById('password'))document.getElementById('password').style.display = '';
        if(document.getElementById('password'))document.getElementById('password').value = document.getElementById('lblpassword').innerHTML;
        if(document.getElementById('address'))document.getElementById('address').style.display = '';
        if(document.getElementById('address'))document.getElementById('address').value = document.getElementById('lbladdress').innerHTML;
        if(document.getElementById('lblusertype')) {
            if(document.getElementById('lblusertype').innerHTML == 'Quản trị') document.getElementById('usertype').value = '1';
            else if(document.getElementById('lblusertype').innerHTML == 'Điều hành') document.getElementById('usertype').value = '2';
            else if(document.getElementById('lblusertype').innerHTML == 'Giúp việc') document.getElementById('usertype').value = '3';
            else if(document.getElementById('lblusertype').innerHTML == 'Khách hàng') document.getElementById('usertype').value = '4';
        }
        if(document.getElementById('usertype'))document.getElementById('usertype').style.display = '';
        if(document.getElementById('picArea'))document.getElementById('picArea').style.display = '';
        if(document.getElementById('fileArea'))document.getElementById('fileArea').style.display = '';
        if(document.getElementById('map'))document.getElementById('map').className = 'enableScroll';
        if(document.getElementById('username'))document.getElementById('username').focus();
    } else {
        if(document.getElementById('lblusername'))document.getElementById('lblusername').style.display = '';
        if(document.getElementById('lblmobile'))document.getElementById('lblmobile').style.display = '';
        if(document.getElementById('lblbirthday'))document.getElementById('lblbirthday').style.display = '';
        if(document.getElementById('lblidnumber'))document.getElementById('lblidnumber').style.display = '';
        if(document.getElementById('lblfamilyMobile'))document.getElementById('lblfamilyMobile').style.display = '';
        if(document.getElementById('lblvideourl'))document.getElementById('lblvideourl').style.display = '';
        if(document.getElementById('lblsalarylevel'))document.getElementById('lblsalarylevel').style.display = '';
        if(document.getElementById('lblamount'))document.getElementById('lblamount').style.display = '';
        if(document.getElementById('lblhome'))document.getElementById('lblhome').style.display = '';
        if(document.getElementById('lbldetail'))document.getElementById('lbldetail').style.display = '';
        if(document.getElementById('lblpassword'))document.getElementById('lblpassword').style.display = '';
        if(document.getElementById('picView'))document.getElementById('picView').style.display = '';
        if(document.getElementById('fileView'))document.getElementById('fileView').style.display = '';
        if(document.getElementById('lblusertype'))document.getElementById('lblusertype').style.display = '';
        if(document.getElementById('lbladdress'))document.getElementById('lbladdress').style.display = '';
        obj.innerHTML = '<span class="fa fa-pencil"></span> Sửa';
        
        if(document.getElementById('saveButton'))document.getElementById('saveButton').style.display = 'none';
        if(document.getElementById('username'))document.getElementById('username').style.display = 'none';
        if(document.getElementById('mobile'))document.getElementById('mobile').style.display = 'none';
        divBirth.style.display = 'none';
        if(document.getElementById('idnumber'))document.getElementById('idnumber').style.display = 'none';
        if(document.getElementById('familymobile'))document.getElementById('familymobile').style.display = 'none';
        if(document.getElementById('videourl'))document.getElementById('videourl').style.display = 'none';      
        if(document.getElementById('salarylevel'))document.getElementById('salarylevel').style.display = 'none';
        if(document.getElementById('amount'))document.getElementById('amount').style.display = 'none';
        if(document.getElementById('home'))document.getElementById('home').style.display = 'none';
        if(document.getElementById('detail'))document.getElementById('detail').style.display = 'none';
        if(document.getElementById('password'))document.getElementById('password').style.display = 'none';
        if(document.getElementById('picArea'))document.getElementById('picArea').style.display = 'none';        
        if(document.getElementById('fileArea'))document.getElementById('fileArea').style.display = 'none';        
        if(document.getElementById('usertype'))document.getElementById('usertype').style.display = 'none';
        if(document.getElementById('address'))document.getElementById('address').style.display = 'none';
        if(document.getElementById('map'))document.getElementById('map').className = 'disableScroll';
    }
}

updateUser = function() {
    $.ajax({ 
        type: 'POST',
        dataType: 'json',
        url: '/updateuser',
        data: $("#updateForm").serialize(),
        cache: false, //fix loop IE
        success: function(data, textStatus, jqXHR) {
            closeAllMessage();
            if(document.getElementById('lblusername'))document.getElementById('lblusername').innerHTML = document.getElementById('username').value;
            if(document.getElementById('lblmobile'))document.getElementById('lblmobile').innerHTML = document.getElementById('mobile').value;
            if(document.getElementById('lblbirthday'))document.getElementById('lblbirthday').innerHTML = document.getElementById('birthday').value;
            if(document.getElementById('lblidnumber'))document.getElementById('lblidnumber').innerHTML = document.getElementById('idnumber').value;
            if(document.getElementById('lblfamilyMobile'))document.getElementById('lblfamilyMobile').innerHTML = document.getElementById('familymobile').value;
            if(document.getElementById('lblvideourl'))document.getElementById('lblvideourl').innerHTML = document.getElementById('videourl').value;
            if(document.getElementById('lblsalarylevel'))document.getElementById('lblsalarylevel').innerHTML = document.getElementById('salarylevel').value;
            if(document.getElementById('lblamount'))document.getElementById('lblamount').innerHTML = document.getElementById('amount').value;
            if(document.getElementById('lbldetail'))document.getElementById('lbldetail').innerHTML = document.getElementById('detail').value;
            if(document.getElementById('lblhome'))document.getElementById('lblhome').innerHTML = document.getElementById('home').value;
            if(document.getElementById('lbladdress'))document.getElementById('lbladdress').innerHTML = document.getElementById('address').value;
            if(document.getElementById('usertype')) {
                if(document.getElementById('usertype').value == '1') document.getElementById('lblusertype').innerHTML = 'Quản trị';
                else if(document.getElementById('usertype').value == '2') document.getElementById('lblusertype').innerHTML = 'Điều hành';
                else if(document.getElementById('usertype').value == '3') document.getElementById('lblusertype').innerHTML = 'Giúp việc';
                else if(document.getElementById('usertype').value == '4') document.getElementById('lblusertype').innerHTML = 'Khách hàng';
            }
            var picture = document.getElementById('imagepath').value;
            if(picture) {
                if(picture != 'null' && picture.length > 0)
                    document.getElementById('picView').innerHTML = '<img src="/image?filename=' + picture + '" style="width:100%; border-radius: 3px;"/>';
            }
            var filepath = document.getElementById('filepath').value;
            if(filepath) {
                if(filepath != 'null' && filepath.length > 0)
                    document.getElementById('fileView').innerHTML = '<a href="/userfile?filename=' + filepath + '" class="btn btn-info"><span class="fa fa-download"></span> Tải xuống </a>';
            }            
            myDropzone.removeAllFiles();
            myFilezone.removeAllFiles();
            editUser();
            successMessage('Ghi lại thành công!');
        }
    });         
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
                    searchUser();
                    return false;
                } else {
                    return true;
                }
            });            
            searchUser();
        }
    }); 
}

setDeletePic = function(obj) {
    if(obj.checked) document.getElementById('deletepic').value = '1';
    else document.getElementById('deletepic').value = '0';
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