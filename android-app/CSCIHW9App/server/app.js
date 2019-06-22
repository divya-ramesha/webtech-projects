var express = require('express');
var app = express();
var http = require('http');
var https = require('https');
var url = require('url');
var request = require('request');
var port = process.env.PORT || 8085;
var API_KEY = "DivyaRam-WebtechH-PRD-916e2f5cf-ae13ed25";
var product_details = {};
var GOOGLE_KEY = "AIzaSyDYM4RlKxcR8BiSnDH-6Lozzq4FPOaI2dM";
var search_engine_id = "002271797951002159810:bplaf-4evoa";

app.get('/', function (req, res) {
	res.send('Successful');
});

app.use(express.static(__dirname));

var getFeedbackRatingStar = function (ratingStar) {
		var result = {
			color: 'gray',
			icon: 'star_border'
		};
		switch (ratingStar) {
			case "Yellow":
				result.color = 'yellow';
				result.icon = 'star_border';
				break;
			case "Blue":
				result.color = 'blue';
				result.icon = 'star_border';
				break;
			case "Turquoise":
				result.color = 'blue';
				result.icon = 'star_border';
				break;
			case "Purple":
				result.color = 'purple';
				result.icon = 'star_border';
				break;
			case "Red":
				result.color = 'red';
				result.icon = 'star_border';
				break;
			case "Green":
				result.color = 'green';
				result.icon = 'star_border';
				break;
			case "YellowShooting":
				result.color = 'yellow';
				result.icon = 'stars';
				break;
			case "TurquoiseShooting":
				result.color = 'blue';
				result.icon = 'stars';
				break;
			case "PurpleShooting":
				result.color = 'purple';
				result.icon = 'stars';
				break;
			case "RedShooting":
				result.color = 'red';
				result.icon = 'stars';
				break;
			case "GreenShooting":
				result.color = 'green';
				result.icon = 'stars';
				break;
			case "SilverShooting":
				result.color = 'silver';
				result.icon = 'stars';
				break;
			case "None":
			default:
				result.color = 'gray';
				result.icon = 'star_border';
		}
		return result;
}

app.get('/getProductsList', function (req, res) {
	res.setHeader("Content-Type", "text/json");
	res.setHeader("Access-Control-Allow-Origin", "*");
	var params = url.parse(req.url, true).query;

	var ebay_api = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + API_KEY + "&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=50&keywords=" + encodeURI(params.keyword);

	if (params.category) {
		ebay_api += "&categoryId=" + params.category;
	}
	var index = 0;
	if (params.distance && parseInt(params.distance) > 0) {
	    ebay_api += "&buyerPostalCode=" + params.zipcode + "&itemFilter(0).name=MaxDistance&itemFilter(0).value=" + params.distance;
	    index += 1;
	}

	if (params.condition) {
		var condition = params.condition.split(",");
		ebay_api += "&itemFilter(" + index + ").name=Condition";
		for (var x = 0; x < condition.length; x++) {
			ebay_api += "&itemFilter(" + index + ").value(" + x + ")=" + condition[x];
		}
		index += 1;
	}
	if (params.shipping) {
		var shipping = params.shipping.split(",");
		for (var x = 0; x < shipping.length; x++) {
			ebay_api += "&itemFilter(" + index + ").name=" + shipping[x] + "&itemFilter(" + index + ").value=true";
			index += 1;
		}
	}
	ebay_api += "&itemFilter(" + index + ").name=HideDuplicateItems&itemFilter(" + index + ").value=true&outputSelector(0)=SellerInfo&outputSelector(1)=StoreInfo";
	//console.log(ebay_api);
	http.get(ebay_api, function (req2, res2) {
		var res_text = "";
		req2.on('data', function (data) {
			res_text += data;
		});
		req2.on('end', function () {
			var jsonobj = JSON.parse(res_text);
			var result = {};
			result['status'] = jsonobj.findItemsAdvancedResponse[0].ack[0].toLowerCase();
			result['statusText'] = "OK";
			if (jsonobj.findItemsAdvancedResponse[0].errorMessage) {
				result['statusText'] = jsonobj.findItemsAdvancedResponse[0].errorMessage[0].error[0].message;
			} else {
				var itemsCount = jsonobj.findItemsAdvancedResponse[0].searchResult[0]['@count'];
				if (itemsCount == "0") {
					result['status'] = "failure";
					result['statusText'] = "No Records";
				} else {
					var items = jsonobj.findItemsAdvancedResponse[0].searchResult[0].item;
					var productArray = [];
					for (index in items) {
						var item = items[index];
						var productObj = {};
						var photo = "N/A";
						if (item.galleryURL && item.galleryURL[0]) {
							photo = item.galleryURL[0];
						}
						var name = "N/A",
							fullTitle = "N/A";
						if (item.title && item.title[0]) {
							var titleLength = item.title[0].length
							if (titleLength > 55) {
								titleLength = 55
							}
							name = item.title[0].substring(0, titleLength) + "...";
							fullTitle = item.title[0];
						}
						var subTitle = "N/A";
						if (item.subtitle && item.subtitle[0]) {
						    subTitle = item.subtitle[0];
						}
						var price = "N/A";
						if (item.sellingStatus && item.sellingStatus[0] && item.sellingStatus[0].currentPrice) {
							price = "$" + item.sellingStatus[0].currentPrice[0].__value__;
						}
						var zipcode = "N/A";
						if (item.postalCode && item.postalCode[0]) {
							zipcode = item.postalCode[0];
						}
						var seller = "N/A";
						if (item.sellerInfo && item.sellerInfo[0] && item.sellerInfo[0].sellerUserName[0]) {
							seller = item.sellerInfo[0].sellerUserName[0].toUpperCase();
						}
						var shipping = "N/A";
						if (item.shippingInfo && item.shippingInfo[0] && item.shippingInfo[0].shippingServiceCost) {
							shippingCost = item.shippingInfo[0].shippingServiceCost[0].__value__;
							if (shippingCost == "0.0") {
								shipping = "Free Shipping";
							} else {
								shipping = "$" + shippingCost;
							}
						}
						if (item.shippingInfo && item.shippingInfo[0]) {
							productObj['shippingInfo'] = item.shippingInfo[0];
						} else {
							productObj['shippingInfo'] = {};
						}
						if (item.returnsAccepted) {
							productObj['shippingInfo']["returnsAccepted"] = item.returnsAccepted;
						}
						if (item.sellerInfo && item.sellerInfo[0]) {
							productObj['sellerInfo'] = item.sellerInfo[0];
							productObj['sellerInfo']['feedbackRatingStar'] = getFeedbackRatingStar(item.sellerInfo[0].feedbackRatingStar[0]);
						} else {
							productObj['sellerInfo'] = {};
						}
						if (item.storeInfo && item.storeInfo[0]) {
							productObj['sellerInfo']["storeInfo"] = item.storeInfo[0];
						}
						var conditionInfo = "N/A";
						if (item.condition && item.condition[0] && item.condition[0].conditionDisplayName[0]) {
                        	conditionInfo = item.condition[0].conditionDisplayName[0];
                        }
						productObj['index'] = parseInt(index) + 1;
						productObj['condition'] = conditionInfo;
						productObj['photo'] = photo;
						productObj['id'] = item.itemId[0];
						productObj['title'] = name;
						productObj['fullTitle'] = fullTitle;
						productObj['price'] = price;
						productObj['zipcode'] = zipcode;
						productObj['seller'] = seller;
						productObj['shipping'] = shipping;
						productObj['itemURL'] = item.viewItemURL[0];
						productObj['subTitle'] = subTitle;
						productArray.push(productObj);
					}
					result['products'] = productArray;
				}
			}

			return res.send(result);
		});
	}).on("error", (err) => {
		console.log("Error: " + err.message);
        return res.send({status:"failure", statusText: "No Records", products:[]});
	});

});

function getProductUsefulFields(body) {
	var jsonObj = JSON.parse(body);

	var result = {};
	var response = {};
	response["status"] = jsonObj.Ack.toLowerCase();
	if (jsonObj.Ack == "Success") {
		jsonObj = jsonObj.Item;
		if (jsonObj.PictureURL && jsonObj.PictureURL.length > 0) {
			result["Photos"] = jsonObj.PictureURL;
		}
		if (jsonObj.Subtitle) {
			result["SubTitle"] = jsonObj.Subtitle;
		}
		if (jsonObj.Title) {
			result["Title"] = jsonObj.Title;
		}
		if (jsonObj.ViewItemURLForNaturalSearch) {
			result["ItemURL"] = jsonObj.ViewItemURLForNaturalSearch;
		}
		if (jsonObj.CurrentPrice && jsonObj.CurrentPrice.Value) {
			result["Price"] = "$" + jsonObj.CurrentPrice.Value;
		}
		if (jsonObj.Location) {
			result["Location"] = jsonObj.Location;
		}
		if (jsonObj.ReturnPolicy) {
			result["ReturnPolicy"] = jsonObj.ReturnPolicy;
		}
		if (jsonObj.GlobalShipping && jsonObj.GlobalShipping === true) {
        	result["GlobalShipping"] = "Yes";
        } else {
            result["GlobalShipping"] = "No";
        }
        if (jsonObj.Storefront) {
            result["Storefront"] = jsonObj.Storefront;
        }
        if (jsonObj.HandlingTime) {
            result["HandlingTime"] = jsonObj.HandlingTime;
        }
		var itemSpecifics = {}
		result["Brand"] = "N/A";
		if (jsonObj.ItemSpecifics && jsonObj.ItemSpecifics.NameValueList) {
			var itemSpecificsArray = jsonObj.ItemSpecifics.NameValueList;
			for (var index in itemSpecificsArray) {
				var specific = itemSpecificsArray[index];
				itemSpecifics[specific.Name] = specific.Value[0];
				if (specific.Name == "Brand")
				    result["Brand"] = specific.Value[0];
			}
		}
		result["ItemSpecifics"] = itemSpecifics;
		response["item"] = result;
	}
	return response;
}

app.get('/getProductPhotos', function (req, res) {
	res.setHeader("Content-Type", "text/json");
	res.setHeader("Access-Control-Allow-Origin", "*");
	var params = url.parse(req.url, true).query;
	var google_api = "https://www.googleapis.com/customsearch/v1?q=" + encodeURI(params.product) + "&cx=" + search_engine_id + "&imgSize=huge&num=8&searchType=image&key=" + GOOGLE_KEY;
	//console.log(google_api);
	https.get(google_api, function (req2, res2) {
		var res_text = "";
		req2.on('data', function (data) {
			res_text += data;
		});
		req2.on('end', function () {
		    var output = JSON.parse(res_text);
		    var response = {status: "failure"};
		    if (output.items) {
		        response.status = "success";
		        response.items = output.items;
		    }
			return res.send(response);
		});

	}).on("error", (err) => {
		console.log("Error: " + err.message);
        return res.send({status:"failure",items:[]});
	});

});

var getSimilarUsefulFields = function (response) {
	response = JSON.parse(response);
	var result = {};
	result["status"] = response.getSimilarItemsResponse.ack.toLowerCase();
	var similarItems = [];
	for (index in response.getSimilarItemsResponse.itemRecommendations.item) {
		var item = response.getSimilarItemsResponse.itemRecommendations.item[index];
		var similarProduct = {};
		similarProduct["image"] = item.imageURL;
		similarProduct["title"] = item.title;
		similarProduct["itemURL"] = item.viewItemURL;
		similarProduct["price"] = parseFloat(item.buyItNowPrice.__value__);
		similarProduct["shipping"] = parseFloat(item.shippingCost.__value__);
		similarProduct["daysLeft"] = parseFloat(item.timeLeft.match(new RegExp("P(.*)D"))[1]);
		similarItems.push(similarProduct);
	}
	if (similarItems.length == 0) {
		result["status"] = "failure";
	}
	result["output"] = similarItems;
	return result;
}

app.get('/getSimilarProducts', function (req, res) {
	res.setHeader("Content-Type", "text/json");
	res.setHeader("Access-Control-Allow-Origin", "*");
	var params = url.parse(req.url, true).query;
	var similar_api = "http://svcs.ebay.com/MerchandisingService?OPERATION-NAME=getSimilarItems&SERVICE-NAME=MerchandisingService&SERVICE-VERSION=1.1.0&CONSUMER-ID=" + API_KEY + "&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&itemId=" + params.itemId + "&maxResults=20";

	http.get(similar_api, function (req2, res2) {
		var res_text = "";
		req2.on('data', function (data) {
			res_text += data;

		});
		req2.on('end', function () {
			return res.send(getSimilarUsefulFields(res_text));
		});

	}).on("error", (err) => {
		console.log("Error: " + err.message);
        return res.send({status:"failure",output:[]});
	});

});

app.get('/getProductDetails', function (req, res) {
	res.setHeader("Content-Type", "text/json");
	res.setHeader("Access-Control-Allow-Origin", "*");
	var params = url.parse(req.url, true).query;
	var ebay_api = "http://open.api.ebay.com/shopping?callname=GetSingleItem&responseencoding=JSON&appid=" + API_KEY + "&siteid=0&version=967&ItemID=" + params.itemId + "&IncludeSelector=Details,ItemSpecifics";
	//console.log(ebay_api);
	http.get(ebay_api, function (req2, res2) {
		var res_text = "";
		req2.on('data', function (data) {
			res_text += data;

		});
		req2.on('end', function () {
			return res.send(getProductUsefulFields(res_text));
		});

	}).on("error", (err) => {
		console.log("Error: " + err.message);
        return res.send({status:"failure",item:[]});
	});

});

app.get('/getZipcodeAutoComplete', function (req, res) {
	res.setHeader("Content-Type", "text/json");
	res.setHeader("Access-Control-Allow-Origin", "*");
	var params = url.parse(req.url, true).query;
	var geo_api = "http://api.geonames.org/postalCodeSearchJSON?postalcode_startsWith="+params.searchText+"&username=dramesha&country=US&maxRows=5";
	http.get(geo_api, function (req2, res2) {
		var res_text = "";
		req2.on('data', function (data) {
			res_text += data;

		});
		req2.on('end', function () {
            var response = JSON.parse(res_text);
            var output = [];
            if (response.postalCodes && response.postalCodes.length > 0) {
                output = response.postalCodes.map(function (item) { return item.postalCode; });
            }
			return res.send(output);
		});

	}).on("error", (err) => {
		console.log("Error: " + err.message);
        return res.send([]);
	});

});

app.get('/searchProducts', function (req, res) {
	res.setHeader("Content-Type", "text/json");
	res.setHeader("Access-Control-Allow-Origin", "*");
	var params = url.parse(req.url, true).query;
	var keyword = "iphone";
	if (params.keyword && params.keyword != "") {
		keyword = encodeURI(params.keyword);
	}
	var ebay_api = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsAdvanced&SERVICE-VERSION=1.0.0&SECURITY-APPNAME=" + API_KEY + "&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&paginationInput.entriesPerPage=20&keywords=" + keyword;
	http.get(ebay_api, function (req2, res2) {
		var res_text = "";
		req2.on('data', function (data) {
			res_text += data;

		});
		req2.on('end', function () {
			return res.send(JSON.parse(res_text));
		});

	}).on("error", (err) => {
		console.log("Error: " + err.message);
	});

});
app.listen(8081);