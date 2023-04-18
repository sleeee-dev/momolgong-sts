let data;


// 메인에 보여질 전체 목록
function drawTable2(list){
	console.log(list[0]);
	document.getElementById("allstudy").innerHTML = ``;
	let title = document.querySelector('#study').value;
	let table = document.createElement("table");  
	table.classList.add('w3-table');

	let tr = document.createElement("tr");
	// InvalidCharacterError: Failed to execute 'add' on 'DOMTokenList': The
	// token provided ('w3-center w3-third') contains HTML space characters,
	// which are not valid in tokens.
	list.forEach(function(item){
		let td = document.createElement("td"); // <td></td>
		td.classList.add('w3-third');
		td.classList.add('w3-center');
		td.classList.add('w3-container'); 
		let img = document.createElement('img'); // <img></img>
		img.src = './images/profile.png';
		// location.href="/StdGroup/insert/" + item['roomNo']
		// "\'location.href=\"/StdGroup/insert/\"" + item['roomNo'] + "\'"
		img.setAttribute('onclick', 'location.href=\"StdGroup/insert/' + item['roomNo'] + '\"');
		img.classList.add('w3-hover-opacity');
		img.setAttribute('width','60%');
		
		let br = document.createElement("br"); // <br>
		let a = document.createElement("a"); // <a></a>
		a.setAttribute('href', 'StdGroup/insert/' + item['roomNo']);
		a.innerText = item['roomTitle'];
		
		let form = document.createElement("form");
		form.action = 'StdList/oneRoom/' + item['roomNo'];
		let button = document.createElement("button");
		button.type = 'submit';
		button.classList.add('w3-button');
		button.classList.add('w3-white');
		button.classList.add('w3-hide-small');
		button.innerText = '정보보기';
		
		
		td.appendChild(img);	// <td><img></img></td>
		td.appendChild(br);	// <td><img></img><br></td>
		td.appendChild(a);	// <td><img></img><br><a></a></td>
		
		br = document.createElement("br");
		
		td.appendChild(br);	// <td><img></img><br><a></a><br></td>
		form.appendChild(button);	// <td><img></img><br><a></a><br></td>
		td.appendChild(form);	// <td><img></img><br><a></a><br></td>
		tr.appendChild(td);		// <tr><td><img></img></td><td><img></img></td></tr>
	})
	
	table.appendChild(tr);
	return document.getElementById("allstudy").appendChild(table);
}



// 메인화면에 보일 추천 스터디
function allStudy(){
	axios({
		method : "GET",
		url : "StdList/allList"
	}).then(function(resData){
		data = resData.data;
		console.log('넘어온 데이터' + data);
		// data타입이 object가 아니면 json이 아닌 예외 메세지가 왔다는 뜻
		if(typeof(data) == "string"){
			alert("실행중 문제 발생 : " + data);
		}else if(typeof(data) == "object") {
			drawTable2(data);
		}
	}).catch(function(errorMsg){
		alert("실행중 문제 발생 : " + errorMsg);
	});
}


