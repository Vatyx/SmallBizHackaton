Parse.initialize("6UQPoyjqD9UoLsUsJMkgdZjEPiFkTWHMzkwL0o4n", 
	"msBjanUIvxmunFO9Gp43aKo0FMrYzOUXn9XqEQLV");
    
$('#button').click(function() {
	var username = $('#inputUsername').val();
	var password = $('#inputPassword').val();
	Parse.User.logIn(username, password, {
  success: function(user) {
  	var elevated =  user.get("privilege");
  	if(elevated == 0)
  	{
		$("#error-code").html("Sorry! That was the incorrect username or password");
  	}
  	else
  	{
  		console.log("Got here");
  		window.location.href='pages/tables.html';
  	}
  },
  error: function(user, error) {
    $("#error-code").html("Sorry! That was the incorrect username or password");
  }
});
	testQuery();
});

function testQuery()
{
	var query = new Parse.Query(Parse.User);
	var currentuser = Parse.User.current();
	var queryEmployerId = currentuser.get("employerId");
	query.equalTo("employerId", queryEmployerId);
	query.equalTo("privilege", 0);
	query.find({
		success: function(results) {
			return results.length;
		},
		error: function(error) {

		}
	});

}

function onRequestUserStats (id) {
	var query = new Parse.Query(Parse.User);
	query.get(id, {
  	success: function(user) {
  		var nameTitle = document.getElementById("nameh1");
  		nameTitle.innerHTML = user.get("name");

    	var stamps = user.get("stamps");
    	var table = document.getElementById("stampTable");
    	var days = [];
    	days.length = 7;
    	for(var i = 0; i < days.length; i++) {
    		days[i] = [];
    	}
    	Parse.Object.fetchAllIfNeeded(stamps, {
    		success: function(list) {
    			list.sort(function(a, b) {
    				return a.createdAt > b.createdAt;
    			});
    			for(var i = 0; i < list.length; i++) {
    				var currentDay = list[i].createdAt.getDay();
    				days[currentDay].push(list[i]);
    			}

    			var highest = 0;
    			for(var i = 0; i < days.length; i++)
    			{
    				if(days[i].length > highest)
    				{
    					highest = days[i].length;
    				}
    			}
    			if(table.rows.length < highest)
    			{
	    			for(var i = table.rows.length; i < highest + 1; i++)
	    			{
	    				table.insertRow();
	    			}
	    			for(var i = 1; i < table.rows.length; i++)
	    			{
	    				for(var j = table.rows[i].cells.length; j < days.length; j++)
	    				{
	    					table.rows[i].insertCell();
	    				}
	    			}
	    		}

    			for(var i = 0; i < days.length; i++)
    			{
    				var totalhours = 0;
    				for(var j = 0; j < days[i].length; j++)
    				{
    					var row = table.rows[j+1];
    					var currentCell = table.rows[j+1].cells[i];
    					var flag = "In:";
    					if(days[i][j].get("flag") == 1)
    					{
    						flag = "Out:";
    					}
    					console.log(flag);
    					var min = days[i][j].createdAt.getMinutes();
    					if(min < 10)
    					{
    						var realmin = "0" + min;
    					}
    					else
    					{
    						var realmin = min.toString();
    					}
    					var hours = days[i][j].createdAt.getHours();
    					var zone = "AM";
    					if(hours > 12)
    					{
    						hours = hours % 12;
    						zone = "PM";
    					}

    					var location = days[i][j].get("location");

    					currentCell.innerHTML = flag + " " + hours + ":" + realmin + " " + zone + "<br />"
    						+ "Loc: (" + Math.floor(location.latitude) + ", " + Math.floor(location.longitude) + ")";

    					
    					if(j%2 == 0 && j != 0)
    					{
    						hours += (days[i][j].createdAt.getTime() - days[i][j-1].createdAt.getTime());
    					}
    				}

    				hours = hours / 3600;
    				var current = document.getElementById(i+"t");
    				var currentHTML = current.innerHTML;
    				currentHTML = currentHTML.split("<br")[0];
    				if(hours)
    					current.innerHTML = currentHTML + "<br />" + "Total Hours: " + Math.floor(hours);
    			}

    		},
    		error: function(error) {
    			console.log("Problem loading user stats: " + error.toString());
    		}
    	});
  	},

  	error: function(user, error) {
    // error is an instance of Parse.Error.
  	}
});
};

$(document).ready(function() {
	var path = window.location.pathname;
	var page = path.split("/").pop();
	console.log( page );
	if(page == "tables.html")
	{
		var numUsers = 0;
		var query = new Parse.Query(Parse.User);
		var currentuser = Parse.User.current();
		var queryEmployerId = currentuser.get("employerId");
		query.equalTo("employerId", queryEmployerId);
		query.equalTo("privilege", 0);
		var names = []
		query.find({
			success: function(results) {
				results.sort(function(a, b) {
					return a.get("name") > b.get("name");
				});
				var list = $("#nameList").append('<ul></ul>').find('ul');
				for (var i = 0; i < results.length; i++) {
					//$('#nameList ul').append($('<li>').text("hello"));
					names[i] = results[i].get("name")
					list.append("<li onclick='onRequestUserStats(this.id)' class='list-group-item no-border' id='" + results[i].id + "'><a href='#'>"+results[i].get("name")+"</a></li>");
				};
			},
			error: function(error) {
	 			console.log("welp");
			}
		});
	}

	$('#').click(function () {
        console.log($(this).text());
        console.log("test");
	});
});