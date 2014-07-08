var FileMultiUpload = (function() {

	function FileMultiUpload(url, bucket_id, descriptions) {

		this.url = url;
		this.bucket_id = bucket_id;
		this.descriptions = descriptions || [];
		this.templates = [];

		this.configureTemplates();
	}

	FileMultiUpload.prototype.configureTemplates = function() {

		var self = this;
		if (Handlebars) {
			Handlebars.registerHelper('formatFileSize', function(size) {
				return self.formatFileSize(size);
			});

			Handlebars.registerHelper('translateUploadError', function(error) {
				return self.translateUploadError(error);
			});

			Handlebars.registerHelper('decodeURI', function(string) {
				return decodeURI(string);
			});

			Handlebars.registerHelper('fileIconType', function(filename) {
				return self.fileIconType(filename);
			});

			if ($('#file-uploading-template').length > 0) {
				this.templates['#uploading'] = Handlebars.compile($(
						'#file-uploading-template').html());
			} else {
				console.log("FileMultiUpload error: No '#file-uploading-template' handlebars template defined");
			}
			if ($('#file-loaded-template').length > 0) {
				this.templates['#loaded'] = Handlebars.compile($(
						'#file-loaded-template').html());
			} else {
				console.log("FileMultiUpload error: No '#file-loaded-template' handlebars template defined");
			}
			if ($('#file-edit-template').length > 0) {
				this.templates['#edit'] = Handlebars.compile($('#file-edit-template').html());
			}

		} else {
			console.log("FileMultiUpload error: No handlebars.js class included.");
		}

	}

	FileMultiUpload.prototype.init = function() {

		var self = this;

		$(document.body).on('click', '#multiupload-files input', function(e) {
			e.stopPropagation();
		});

		$(document.body)
				.on(
						'click',
						'#multiupload-files button.delete',
						function(e) {

							button = $(this);
							$
									.ajax({
										url : button.attr("my-data-url"),
										type : button.attr("my-data-type")
									})
									.done(
											function() {
												button.closest(
														'.template-download')
														.remove();
												if ($('#multiupload-files .template-download').length == 0) {
													hide_file_rights();
												}
											});
							e.stopPropagation();
						});

		$('#fileupload')
				.fileupload(
						{
							url : self.url,
							formData : {},
							multipart : false,
							maxChunkSize : 500000,
							dataType : 'json',
							previewMaxWidth : 100,
							previewMaxHeight : 100,
							previewCrop : true,
							chunkdone : function(e, data) {
								console.log(data.jqXHR.status);
								if (data.jqXHR.status === 201) {
									data.url = data.jqXHR
											.getResponseHeader('Location');
									data.type = 'PUT';
								}
							}
						})
				.on('fileuploadadd', function(e, data) {

					data.context = $(self.templates['#uploading'](data));
					data.context.appendTo('#multiupload-files');
					$(".form-action-buttons").attr("disabled", "disabled");

				})
				.on(
						'fileuploadprocessalways',
						function(e, data) {
							var index = data.index, file = data.files[index], node = $(data.context
									.children()[index]);
							if (file.preview) {
								node.prepend(file.preview);
							}
							if (file.error) {
								node.append($('<span class="text-danger"/>')
										.text(file.error));
							}
						})
				.on(
						'fileuploadprogress',
						function(e, data) {
							var progress = Math.floor(data.loaded / data.total
									* 100);
							if (data.context) {
								data.context.each(function() {
									$(this).find('.progress').attr(
											'aria-valuenow', progress)
											.children().first().css('width',
													progress + '%');
								});
							}
						})
				.on(
						'fileuploadprogressall',
						function(e, data) {
							var progress = parseInt(data.loaded / data.total
									* 100, 10);
							jQuery('#progress .progress-bar').css('width',
									progress + '%');
							if (progress >= 100) {
								$(".form-action-buttons")
										.removeAttr("disabled");
							}
						})
				.on(
						'fileuploaddone',
						function(e, data) {

							if (data.context) {

								data.context.each(function(index) {

									var context_file = $(this);
									var file = data.result.files[index];
									file.description = self
											.getDescription(file.id);
									var object = {
										files : [ file ]
									};
									$(self.templates['#loaded'](object))
											.replaceAll(context_file);

								});

							} else {
								$('#multiupload-files').html(
										$(self.templates['#loaded']
												(data.result.files)));
							}
							show_file_rights();

						})
				.on(
						'fileuploadfail',
						function(e, data) {

							if (data.context) {

								data.context
										.each(function(index) {

											var context_file = $(this);
											var file = data._response.jqXHR.responseJSON.files[index]
													|| data.files[index];
											file.description = self
													.getDescription(file.id);
											var object = {
												files : [ file ]
											};
											$(self.templates['#loaded'](object))
													.replaceAll(context_file);

										});

							} else {
								$('#multiupload-files')
										.html(
												$(self.templates['#loaded']
														(data.files)));
							}
							show_file_rights();

						});
	}

	FileMultiUpload.prototype.loadDataFromContext = function(data) {

		$('#multiupload-files').append($(this.templates['#edit'](data)));
		if (data.files.length > 0) {
			show_file_rights(true);
		}
	}

	FileMultiUpload.prototype.loadDataFromBucket = function() {

		var self = this;
		$.ajax({
			url : self.url,
			dataType : "json"
		}).done(
				function(data) {
					$(data.files)
							.each(
									function(index) {
										var file = $(data.files)[index];
										file.description = self
												.getDescription(file.id);
										var object = {
											files : [ file ]
										};
										$('#multiupload-files').append(
												$(self.templates['#loaded']
														(object)));
									});
					if (data.files.length > 0) {
						jQuery('#progress .progress-bar').css('width', '100%');
						show_file_rights();
					}
				});
	}

	FileMultiUpload.prototype.getDescription = function(index) {

		return this.descriptions[index] || "";
	}

	FileMultiUpload.prototype.fileIconType = function(filename) {

		dot = filename.lastIndexOf(".") || 0;
		if (dot > 0) {
			ext = filename.substring(dot).toLowerCase();
			if (ext == ".doc" || ext == ".xdoc" || ext == ".odt") {
				return "word";
			}
			if (ext == ".jpg" || ext == ".png" || ext == ".gif") {
				return "image";
			}
			if (ext == ".rtf") {
				return "text";
			}
			if (ext == ".xls") {
				return "excel";
			}
			if (ext == ".pdf") {
				return "pdf";
			}
		}
		return "";
	}

	FileMultiUpload.prototype.translateUploadError = function(uploadError) {

		var translateUploadError = {
			'ITEM_COUNT_EXCEEDED' : 'Przekroczono ilość plików do podłączenia.',
			'ITEM_SIZE_EXCEEDED' : 'Zbyt duży rozmair pliku.',
			'FORMAT_NOT_ALLOWED' : 'Niedozwolony format pliku'
		};

		return translateUploadError[uploadError] || message;
	}

	FileMultiUpload.prototype.formatFileSize = function(bytes) {

		if (typeof bytes !== 'number') {
			return '';
		}
		if (bytes >= 1000000000) {
			return (bytes / 1000000000).toFixed(2) + ' GB';
		}
		if (bytes >= 1000000) {
			return (bytes / 1000000).toFixed(2) + ' MB';
		}
		return (bytes / 1000).toFixed(2) + ' KB';
	}

	return FileMultiUpload;

})();