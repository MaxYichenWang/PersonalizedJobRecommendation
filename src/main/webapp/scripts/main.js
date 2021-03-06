// Use Immediately Invoked Function Expression (IIFE), 立即执行函数
// Do not define a global function, because it may conflict with other .js files
(function () {
    // Default variables
    var user_id = '1111';
    var user_fullname = 'John Smith';
    var lng = -71.06;
    var lat = 42.43;

    // Initialize major event handlers
    function init() {
        // add event listeners, when users interact with button and link

        // click "Back to Login" login form button -> check if session is valid
        document.querySelector('#login-form-btn').addEventListener('click', onSessionInvalid);

        // click "Login" button -> log in current user
        document.querySelector('#login-btn').addEventListener('click', login);

        // click "New User? Register" register form button -> display register form
        document.querySelector('#register-form-btn').addEventListener('click', showRegisterForm);

        // click "Register" register button -> display create new user page
        document.querySelector('#register-btn').addEventListener('click', register);

        // click "NearBy" button -> load near by items
        document.querySelector('#nearby-btn').addEventListener('click', loadNearbyItems);

        // click "Favorite" button
        document.querySelector('#fav-btn').addEventListener('click', loadFavoriteItems);

        // click "Recommendation" button
        document.querySelector('#recommend-btn').addEventListener('click', loadRecommendedItems);

        // check if current user logged in before, if current session valid
        validateSession();

    }

    // Initially we enter the session invalid state,
    // then we use the GET HTTP request to ask the “login” servlet if we already have a valid session on the server,
    // if so we enter the session valid state.
    function validateSession() {
        // assume invalid session, no user login, display only login form
        onSessionInvalid();

        // call "doGET" in "LoginServlet"
        var url = './login';
        var req = JSON.stringify({});

        // display loading messages
        showLoadingMessage("Validating session...")

        // make AJAX call, use "doGET" in "LoginServlet" to check if any user already logged in
        ajax('GET', url, req,
            // session is still valid
            function(res) {
                var result = JSON.parse(res);

                // if logged in, display logged elements
                if (result.status === 'OK') {
                    onSessionValid(result);
                }
            }, function(){
                console.log('login error');
            });
    }

    function showLoadingMessage(msg) {
        var itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' +
            msg + '</p>';
    }

    /**
     * When the session is invalid - before we login
     * we should only show the login form
     */
    function onSessionInvalid() {
        // in the DOM tree, select element by "#id"
        var loginForm = document.querySelector('#login-form');
        var registerForm = document.querySelector('#register-form');
        var itemNav = document.querySelector('#item-nav');
        var itemList = document.querySelector('#item-list');
        var avatar = document.querySelector('#avatar');
        var welcomeMsg = document.querySelector('#welcome-msg');
        var logoutBtn = document.querySelector('#logout-link');

        // hide elements which are only displayed after login
        hideElement(itemNav);
        hideElement(itemList);
        hideElement(avatar);
        hideElement(logoutBtn);
        hideElement(welcomeMsg);
        hideElement(registerForm);

        // clear login error information
        clearLoginError();
        // only display login-form before login
        showElement(loginForm);
    }

    // clear previous login error information
    function clearLoginError() {
        document.querySelector('#login-error').innerHTML = '';
    }

    // only display register form
    function showRegisterForm() {
        var loginForm = document.querySelector('#login-form');
        var registerForm = document.querySelector('#register-form');
        var itemNav = document.querySelector('#item-nav');
        var itemList = document.querySelector('#item-list');
        var avatar = document.querySelector('#avatar');
        var welcomeMsg = document.querySelector('#welcome-msg');
        var logoutBtn = document.querySelector('#logout-link');

        hideElement(itemNav);
        hideElement(itemList);
        hideElement(avatar);
        hideElement(logoutBtn);
        hideElement(welcomeMsg);
        hideElement(loginForm);

        clearRegisterResult();
        showElement(registerForm);
    }

    // clear previous register results
    function clearRegisterResult() {
        document.querySelector('#register-result').innerHTML = '';
    }

    // hide unnecessary elements
    function hideElement(element) {
        // change inline style
        element.style.display = 'none';
    }

    // display elements
    function showElement(element, style) {
        var displayStyle = style ? style : 'block';
        element.style.display = displayStyle;
    }


    /* SIGN UP*/
    // create new account
    function register() {
        // select elements and content
        var username = document.querySelector('#register-username').value;
        var password = document.querySelector('#register-password').value;
        var firstName = document.querySelector('#register-first-name').value;
        var lastName = document.querySelector('#register-last-name').value;

        //
        if (username === "" || password == "" || firstName === "" || lastName === "") {
            showRegisterResult('Please fill in all fields');
            return
        }

        // use regex (Regular Expression) to check if username valid
        if (username.match(/^[a-z0-9_]+$/) === null) {
            showRegisterResult('Invalid username');
            return
        }

        // use md5 hashing, imported from external source
        password = md5(username + md5(password));

        // The request parameters
        // "RegisterServlet"
        var url = './register';
        var req = JSON.stringify({
            user_id : username,
            password : password,
            first_name: firstName,
            last_name: lastName,
        });

        // call POST api "doPost" in "RegisterServlet"
        ajax('POST', url, req,

            // successful callback
            function(res) {
                var result = JSON.parse(res);

                // successfully register a new account
                if (result.result === 'OK') {
                    showRegisterResult('Successfully registered');
                } else {
                    showRegisterResult('User already existed');
                }
            },

            // error in request
            function() {
                showRegisterResult('Failed to register');
            });
    }

    // display register information
    function showRegisterResult(registerMessage) {
        document.querySelector('#register-result').innerHTML = registerMessage;
    }

    // ajax helper function (Asynchronous Javascript And XML)
    // frontend use ajax to acquire data from backend while not interrupting the internet communication
    function ajax(method, url, data, successCallback, errorCallback) {
        // create a new request
        var xhr = new XMLHttpRequest();

        // method = GET/POST/DELETE...
        xhr.open(method, url, true);

        //
        xhr.onload = function() {
            // if request success
            if (xhr.status === 200) {
                successCallback(xhr.responseText);
            // if request fail
            } else {
                errorCallback();
            }
        };

        // if error
        xhr.onerror = function() {
            console.error("The request couldn't be completed.");
            errorCallback();
        };

        // send request
        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader("Content-Type",
                "application/json;charset=utf-8");
            xhr.send(data);
        }
    }


    /* LOG IN */
    // get values of username and password entered by user
    function login() {
        var username = document.querySelector('#username').value;
        var password = document.querySelector('#password').value;

        // use md5 encode password
        password = md5(username + md5(password));

        // The request parameters
        // "LoginServlet"
        var url = './login';
        var req = JSON.stringify({
            user_id : username,
            password : password,
        });

        // call "doPOST" in "LoginServlet"
        ajax('POST', url, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);

                // successfully logged in
                if (result.status === 'OK') {
                    onSessionValid(result);
                }
            },

            // error
            function() {
                showLoginError();
            });
    }

    // display login error
    function showLoginError() {
        document.querySelector('#login-error').innerHTML = 'Invalid username or password';
    }

    // display elements when user is logged in
    function onSessionValid(result) {
        user_id = result.user_id;
        user_fullname = result.name;

        var loginForm = document.querySelector('#login-form');
        var registerForm = document.querySelector('#register-form');
        var itemNav = document.querySelector('#item-nav');
        var itemList = document.querySelector('#item-list');
        var avatar = document.querySelector('#avatar');
        var welcomeMsg = document.querySelector('#welcome-msg');
        var logoutBtn = document.querySelector('#logout-link');

        welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

        showElement(itemNav);
        showElement(itemList);
        showElement(avatar);
        showElement(welcomeMsg);
        showElement(logoutBtn, 'inline-block');
        hideElement(loginForm);
        hideElement(registerForm);

        // get user's geo information
        initGeoLocation();
    }

    /* GET GEO INFO */
    // Define the follow functions to load user’s GEO information
    // we will try to get the GEO info from the navigator.geolocation,
    // on success we update the var “lat” and “lng”,
    // on failure we get the GEO info from user’s IP address
    function initGeoLocation() {
        // BOM, user browser's function navigator to get geo information
        // if success
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                onPositionUpdated,
                onLoadPositionFailed, {
                    maximumAge: 60000
                });
            showLoadingMessage('Retrieving your location...');
        // if fail
        } else {
            onLoadPositionFailed();
        }
    }

    // if get user's position, get latitude and longitude
    function onPositionUpdated(position) {
        // lat = position.coords.latitude;
        // lng = position.coords.longitude;

        // display nearby items
        loadNearbyItems();
    }

    // if cannot get user's position, get position based on user's IP address
    function onLoadPositionFailed() {
        console.warn('navigator.geolocation is not available');
        getLocationFromIP();
    }

    // get position from IP address
    function getLocationFromIP() {
        // get location from http://ipinfo.io/json
        var url = 'http://ipinfo.io/json'
        var data = null;

        // make AJAX request
        ajax('GET', url, data, function(res) {
            var result = JSON.parse(res);
            if ('loc' in result) {
                var loc = result.loc.split(',');
                lat = loc[0];
                lng = loc[1];
            } else {
                console.warn('Getting location by IP failed.');
            }

            // display nearby items
            loadNearbyItems();
        });
    }

    /* LOAD USER'S NEARBY ITEMS */
    /**
     * API #1 Load the nearby items API end point: [GET]
     * /search?user_id=1111&lat=37.38&lon=-122.08
     */
    function loadNearbyItems() {
        console.log('loadNearbyItems');

        activeBtn('nearby-btn');

        // The request parameters
        var url = './search';
        var params = 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
        var data = null;

        // display loading message
        showLoadingMessage('Loading nearby items...');

        // make AJAX call, "doGET" in "SearchServlet"
        ajax('GET', url + '?' + params, data,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No nearby item.');
                } else {
                    // display items
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load nearby items.');
            }
        );
    }

    /**
     * A helper function that makes a navigation button active
     *
     * @param btnId - The id of the navigation button
     */
    function activeBtn(btnId) {
        var btns = document.querySelectorAll('.main-nav-btn');

        // deactivate all navigation buttons
        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // active the one that has id = btnId
        var btn = document.querySelector('#' + btnId);
        btn.className += ' active';
    }

    // display warning message
    function showWarningMessage(msg) {
        var itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' +
            msg + '</p>';
    }

    // display error message
    function showErrorMessage(msg) {
        var itemList = document.querySelector('#item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' +
            msg + '</p>';
    }

    /**
     * List recommendation items base on the data received
     *
     * @param items - An array of item JSON objects
     */
    function listItems(items) {
        var itemList = document.querySelector('#item-list');
        itemList.innerHTML = ''; // clear current results

        for (var i = 0; i < items.length; i++) {
            addItem(itemList, items[i]);
        }
    }

    /**
     * Add a single item to the list
     *
     * @param itemList - The <ul id="item-list"> tag (DOM container)
     * @param item - The item data (JSON object)
     *
     */
    function addItem(itemList, item) {
        var item_id = item.id;

        // create the <li> tag and specify the id and class attributes
        var li = $create('li', {
            id : 'item-' + item_id,
            className : 'item'
        });

        // set the data attribute ex. <li data-item_id="G5vYZ4kxGQVCR"
        // data-favorite="true">
        li.dataset.item_id = item_id;
        li.dataset.favorite = item.favorite;

        // item image
        if (item.company_logo) {
            li.appendChild($create('img', {
                src : item.company_logo
            }));
        } else {
            li.appendChild($create('img', {
                src : 'https://via.placeholder.com/100'
            }));
        }
        // section
        var section = $create('div');

        // title
        var title = $create('a', {
            className : 'item-name',
            href : item.url,
            target : '_blank'
        });
        title.innerHTML = item.title;
        section.appendChild(title);

        // keyword
        var keyword = $create('p', {
            className : 'item-keyword'
        });
        keyword.innerHTML = 'Keyword: ' + item.keywords.join(', ');
        section.appendChild(keyword);

        li.appendChild(section);

        // address
        var address = $create('p', {
            className : 'item-address'
        });

        // ',' => '<br/>', '\"' => ''
        address.innerHTML = item.location.replace(/,/g, '<br/>').replace(/\"/g,
            '');
        li.appendChild(address);

        // favorite link
        var favLink = $create('p', {
            className : 'fav-link'
        });

        favLink.onclick = function() {
            changeFavoriteItem(item);
        };

        favLink.appendChild($create('i', {
            id : 'fav-icon-' + item_id,
            className : item.favorite ? 'fa fa-heart' : 'fa fa-heart-o'
        }));

        li.appendChild(favLink);

        itemList.appendChild(li);
    }

    /**
     * A helper function that creates a DOM element <tag options...>
     * @param tag
     * @param options
     * @returns {Element}
     */
    function $create(tag, options) {
        var element = document.createElement(tag);
        for (var key in options) {
            if (options.hasOwnProperty(key)) {
                element[key] = options[key];
            }
        }
        return element;
    }

    /**
     * API #4 Toggle favorite (or visited) items
     *
     * @param item - The item from the list
     *
     * API end point: [POST]/[DELETE] /history request json data: {
     * user_id: 1111, favorite: item }
     */
    function changeFavoriteItem(item) {
        // check whether this item has been visited or not
        var li = document.querySelector('#item-' + item.id);
        var favIcon = document.querySelector('#fav-icon-' + item.id);
        var favorite = !(li.dataset.favorite === 'true');

        // request parameters
        var url = './history';
        var req = JSON.stringify({
            user_id: user_id,
            favorite: item
        });
        var method = favorite ? 'POST' : 'DELETE';

        ajax(method, url, req,
            // successful callback
            function(res) {
                var result = JSON.parse(res);
                if (result.status === 'OK' || result.result === 'SUCCESS') {
                    li.dataset.favorite = favorite;
                    favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
                }
            });
    }


    /**
     * API #2 Load favorite (or visited) items API end point: [GET]
     * /history?user_id=1111
     */
    function loadFavoriteItems() {
        activeBtn('fav-btn');

        // request parameters
        var url = './history';
        var params = 'user_id=' + user_id;
        var req = JSON.stringify({});

        // display loading message
        showLoadingMessage('Loading favorite items...');

        // make AJAX call
        ajax('GET', url + '?' + params, req, function(res) {
            var items = JSON.parse(res);
            if (!items || items.length === 0) {
                showWarningMessage('No favorite item.');
            } else {
                listItems(items);
            }
        }, function() {
            showErrorMessage('Cannot load favorite items.');
        });
    }

    /**
     * API #3 Load recommended items API end point: [GET]
     * /recommendation?user_id=1111
     */
    function loadRecommendedItems() {
        activeBtn('recommend-btn');

        // request parameters
        var url = './recommendation' + '?' + 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
        var data = null;

        // display loading message
        showLoadingMessage('Loading recommended items...');

        // make AJAX call
        ajax('GET', url, data,
            // successful callback
            function(res) {
                var items = JSON.parse(res);
                if (!items || items.length === 0) {
                    showWarningMessage('No recommended item. Make sure you have favorites.');
                } else {
                    listItems(items);
                }
            },
            // failed callback
            function() {
                showErrorMessage('Cannot load recommended items.');
            }
        );
    }


    // IIFE function executes
    init();
})();