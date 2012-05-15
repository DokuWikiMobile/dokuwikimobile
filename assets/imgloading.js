function updateImage(id, base64content, type) {
	$('img[data-imgid="' + id + '"]').attr('src', 'data:'+type+';base64,'+base64content);
}