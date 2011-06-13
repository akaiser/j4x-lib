/*
 * jQuery UI XDataTable
 *
 * This widget facilitates the transport and representation of data
 *
 * Copyright 2011, Albert Kaiser
 * under the GPL Version 2 licenses.
 *
 * @author akaiser
 * @version 0.8 (03.01.11)
 *
 * http://j4x.org
 *
 * Depends:
 *
 *  - jQuery Plugins
 *  jquery.delay.js
 *  jquery.json.js
 *  jquery.xoverlay.js
 *  
 *  - jQuery UI js
 *  jquery.ui.core.js
 *  jquery.ui.widget.js
 *  jquery.ui.button.js
 *
 *  - jQuery UI css
 *  jquery.ui.core.css
 *  jquery.ui.theme.css
 *  jquery.ui.button.css
 *
 *  - j4x UI css
 *  jquery.ui.xdatatable.css
 */
(function($) {
    $.widget( 'ui.xdatatable', {

        _pe: null,
        _xdatatable: null,
        _xoverlay: null,
        _selectedRow: null,
        _cellAlignArray: [],
        
        _create: function() {
            this._createTable();
            this._createFilter();
        },

        _init: function() {
            
            // init overlay functionality
            this._xoverlay = $.xoverlay(this.element);

            this._call('INIT',{
                rowcount: this.options.rowcount,
                sort: this.options.sort,
                filter: this.options.filter
            });
        },

        _createTable: function() {
            var self = this, o = this.options;

            /**
             * header creation
             */
            var headerRow = $('<tr />');

            $(o.column).each(function(i,e){

                var column = $('<th />');
                var button = $('<button>'+e.label+'</button>').button();

                // check for width attr.
                if(e.width){
                    $(column).attr('width',e.width);
                }

                // check for alignment attr.
                if(e.align){

                    // access for body cells
                    self._cellAlignArray[i]=e.align;
                }

                // check for sortable
                if(e.sortpath){
                    $(button).attr('id',e.sortpath);

                    $(button).click(function() {

                        // perform sort-event
                        self._call('SORT', {
                            sortpath: e.sortpath
                        });
                    });

                }else{
                    $(button).button('disable');
                }

                headerRow.append(column.append(button));
            });
            
            /**
             * footer creation
             */
            var paging = (o.paging)?true:false;

            // pagiator elements
            this._pe = [
            $('<span title="INFO" class="ui-xdatatable_footer_span">'
                + ((paging && o.paging.lbl_entry)?o.paging.lbl_entry:'entry') + ': <span /> | '
                + ((paging && o.paging.lbl_page)?o.paging.lbl_page:'page') + ': <span />' 
                + '</span>'),
            $('<button title="FIRST" />').button({
                label: (paging && o.paging.lbl_first)?o.paging.lbl_first:'first',
                icons:{
                    primary: 'ui-icon-arrowthickstop-1-w'
                }
            }),
            $('<button title="PREVIOUS" />').button({
                label: (paging && o.paging.lbl_previous)?o.paging.lbl_previous:'previous',
                icons:{
                    primary: 'ui-icon-arrowthick-1-w'
                }
            }),
            $('<button title="NEXT" />').button({
                label: (paging && o.paging.lbl_next)?o.paging.lbl_next:'next',
                icons:{
                    secondary: 'ui-icon-arrowthick-1-e'
                }
            }),
            $('<button title="LAST" />').button({
                label: (paging && o.paging.lbl_last)?o.paging.lbl_last:'last',
                icons:{
                    secondary: 'ui-icon-arrowthickstop-1-e'
                }
            }),
            $('<button title="RELOAD" />').button({
                label: (paging && o.paging.lbl_reload)?o.paging.lbl_reload:'reload'
            })
            ];

            // container for paging elements
            var pagingDiv = $('<div align="right" />');

            // fill the paging container
            $(this._pe).each(function(i,e){

                // only buttons are click- and removeable
                if(i!==0){
                    $(e).click(function(){

                        // perform paging-event
                        self._call('PAGING', {
                            event: $(e).attr('title')
                        });
                    });

                    if(!o.rowcount){
                        $(e).css('display','none');
                    }
                }
                $(pagingDiv).append(e);
            });

            /**
             * table creation
             */
            this._xdatatable = $(
                '<table class="ui-widget ui-xdatatable" cellpadding="0" cellspacing="0">'
                +'<thead class="ui-widget-header"></thead>'
                +'<tbody class="ui-widget-content"></tbody>'
                +'<tfoot class="ui-widget-header"></tfoot>'
                +'</table>');

            // append head and foot
            $('thead',this._xdatatable).append(headerRow);
            $('tfoot',this._xdatatable).append($('<tr />')
                .append($('<td />').attr('colspan', $('th', headerRow).length)
                    .append(pagingDiv)));

            // finally append table to this element
            this.element.append(this._xdatatable);
        },

        _createFilter: function() {
            var self = this;

            // prepare filter
            var filterElement, filterValue;

            $(this.options.filter).each(function(i,e){

                filterElement = $('#'+e.id);
                filterValue = $.trim($(filterElement).val());

                // identify filtertype and bind event
                switch($(filterElement).attr('tagName')){
                    case 'INPUT':

                        // initial param setup
                        e.filtertype = 'INPUT';

                        // KeyUp or OnChange
                        if(e.keyup){
                            $(filterElement).delay('keyup', function() {
                                self._performInputEvent(e,this);
                            },300);
                        }else{
                            $(filterElement).change(function(){
                                self._performInputEvent(e,this);
                            });
                        }

                        // label attr is available
                        if(e.label){

                            // bind events for label setup
                            $(filterElement).blur(function(){
                                if($.trim($(this).val()).length==0){
                                    $(this).val(e.label).addClass('ui-xdatatable_input_label_text');
                                }
                            }).focus(function(){
                                if($.trim($(this).val())==e.label){
                                    $(this).val('').removeClass('ui-xdatatable_input_label_text');
                                }
                            });

                            // check the init value (firefox reload fix)
                            if(e.label != filterValue){

                                // set the (maybe defined) init value
                                e.filtervalue = filterValue;
                            }
                        }else{

                            // set the (maybe defined) init value
                            e.filtervalue = filterValue;
                        }

                        // @todo: hier soll irgendwie Suggest-Implementierung stattfinden,


                        /*
                        // some ui settings?
                        $(filterElement).addClass('ui-state-default')
                        .focus(function(){
                            $(this).addClass('ui-state-highlight');
                        })
                        .blur(function(){
                            $(this).removeClass('ui-state-highlight');
                        });
                        */
                        //e.filtertype = 'SUGGEST';
                        break;
                    case 'SELECT':
                        if($(filterElement).attr('multiple')){
                            e.filtertype = 'SELECTMULTIPLE';
                        }else{
                            e.filtertype = 'SELECTONE';

                            $(filterElement).change(function(){
                                e.filtervalue = $.trim($(this).val());
                                //e.filtervalue = $.trim($(this).attr('value'));
                                self._call('FILTER', e);
                            });
                           
                        //interesting for multiselect
                        //var str = '';
                        //$('#'+this[0]+' option:selected').each(function () {
                        //str += $(this).text() + ' ';
                        //});
                        }
                        break;
                }
            });
        },

        _performInputEvent: function(filter,element) {

            var filterValue = $.trim($(element).val());
            var valueLength = filterValue.length;

            // more than two char and diff. value
            if((valueLength == 0 || valueLength > 1)
                && (filter.filtervalue != filterValue)){

                // only if there is no label and labelval. is not the value
                if(!filter.label || (filter.label && filter.label != filterValue)){
                    filter.filtervalue = filterValue;
                    this._call('FILTER', filter);
                }
                $(element).removeClass('ui-state-error-text');
            }else{
                $(element).addClass('ui-state-error-text');
            }
        },

        /*
         * @param {String} requestType
         * @param {Object} requestParams @todo describe
         */
        _call: function(requestType, requestParams) {
            var self = this;

            // show overlay
            self._xoverlay.show(true);
            
            var toJSON = $.toJSON(requestParams);
            
            switch(typeof self.options.datasource){
                
                // javascript object request
                case 'object':{
                    self.options.datasource.getContent(requestType,toJSON,
                        function(responce){
                            self._restore(requestType, requestParams.event, $.parseJSON(responce));
                        });
                        
                    break;
                }
                
                // servlet or remote page request
                case 'string':{
                    $.ajax({
                        url: self.options.datasource,
                        dataType: "json",
                        data: "requestType="+requestType+"&requestParams="+toJSON,
                        success: function(responce){
                            self._restore(requestType, requestParams.event, responce);
                        }
                    });
                    
                    break;
                }
            }
        },
        
        _restore: function(requestType, eventType, responce) {
            this._restoreBody(responce[0]);
            this._restoreHeader(responce[1], responce[0].length);
            this._restoreFooter(responce[2]);
            this._restoreFilter(responce[3], requestType, eventType);

            // hide overlay
            this._xoverlay.show(false);
        },

        // @todo describe params
        _restoreBody: function(params) {
            var self = this;
            var newRow = null, newCell = null;

            // create a table body
            var tableBody = $('<tbody class="ui-widget-content" />');

            // iterate each row entry
            $(params).each(function(){

                // create new row
                newRow = $('<tr />');

                // iterate each column
                $(this).each(function(i,e){

                    // append the object id
                    if(i==0){
                        $(newRow).attr('id', e);
                    }else{

                        newCell = $('<td>'+e+'</td>');

                        if(self._cellAlignArray[i-1]){
                            $(newCell).css('text-align',self._cellAlignArray[i-1]);
                        }

                        $(newRow).append(newCell);
                    }
                });

                // append row to the body
                $(newRow).appendTo(tableBody);
            });

            // add alternate row background
            $('tr:odd', tableBody).addClass('ui-state-default');

            // add hover and select binding
            $('tr', tableBody)
            .hover(function(){
                $(this).toggleClass('ui-state-hover');
            })
            .click(function(){

                $(this).toggleClass('ui-xdatatable_selected_row');

                // switch selected row
                $(self._selectedRow).removeClass('ui-xdatatable_selected_row');
                self._selectedRow = $(this).addClass('ui-xdatatable_selected_row');
            });

            // replace current body
            $('tbody', self._xdatatable).replaceWith(tableBody);
        },


        // @todo describe params
        _restoreHeader: function(params, entries) {
            var self = this;

            // header-columns of the table
            var headerColumns = $('thead th', this._xdatatable);

            $(headerColumns).each(function(i,e){

                // identify the nested button
                var currentButton = $(e).find('button');

                // sorting only possible with multiple entries
                if(entries > 1){

                    var path = $(currentButton).attr('id');

                    // sorting only possible if there is a path
                    if(path){

                        // enable the button
                        self._toggleButtonStatus(currentButton, true);

                        // current column is responsible for sorting
                        if(params[1] != null && path == params[0]){

                            // add correct sort icon
                            if(params[1]){
                                $(currentButton).button('option','icons',{
                                    secondary:'ui-icon-carat-1-s'
                                });
                            }else{
                                $(currentButton).button('option','icons',{
                                    secondary:'ui-icon-carat-1-n'
                                });
                            }
                        }else{

                            // handle other columns/buttons and remove the icon
                            if($(currentButton).button('option','icons').secondary){
                                $(currentButton).button('option','icons',{
                                    secondary:null
                                });
                            }
                        }
                    }
                }else{

                    // disable the button
                    self._toggleButtonStatus(currentButton, false);
                }

            // remove hover and focus
            //self._removeHoverAndFocus(currentButton);
            });
        },
        
        // @todo describe params
        _restoreFooter: function(params) {
            var self = this;

            $(params).each(function(i,e){
                
                // get the info
                if(i===0){
                    var spans = $('span',self._pe[i]), values = e.split('|');
                    $(spans[0]).text(values[0]);
                    $(spans[1]).text(values[1]);
                }else{

                    // remove hover and focus
                    self._removeHoverAndFocus(self._pe[i]);

                    // de/activate buttons
                    self._toggleButtonStatus(self._pe[i], e);
                }
            });

            // also for refresh-button
            self._removeHoverAndFocus(self._pe[5]);
        },

        // @todo describe params
        _restoreFilter: function(params, requestType, eventType) {
            var self = this;

            $(params).each(function(i,e){

                // Das Select-Element
                var filterElement = $('#' + e[0]);

                // check the event
                switch(e[1]){

                    // getting the values (IE and Chrome workaround)
                    case 'INPUT':

                        // updating only on INIT or PAGING-RELOAD event
                        if(requestType === 'INIT' || eventType === 'RELOAD'){

                            // set the value
                            $(filterElement).val(e[2]);

                            // init the filter config
                            $(self.options.filter).each(function(i,f){

                                // there is a label attr and filter is present
                                if(f.label && f.id === e[0]){
                                    
                                    var filterValue = $.trim($(filterElement).val());

                                    // set the default label
                                    if(filterValue.length === 0){
                                        $(filterElement)
                                        .val(f.label)
                                        .addClass('ui-xdatatable_input_label_text');
                                    }
                                }
                            });
                        }

                        break;

                    // create select options and set the selection
                    case 'SELECTONE': {

                        var options = '', selectedValues = e[3], selectedAttribute, selected = false;

                        $(e[2]).each(function() {

                            var value = this.toString();

                            selectedAttribute = '';

                            if(value === selectedValues){
                                selectedAttribute = 'selected="selected" ';
                                selected = true;
                            }

                            // create options
                            options += '<option '+selectedAttribute+'value="'+value+'">'+ value +'</option>';
                        });

                        // selection already done
                        if(selected){
                            selectedAttribute = '';
                        }else{
                            selectedAttribute = 'selected="selected" ';
                        }

                        // add empty option
                        options = '<option '+selectedAttribute+'></option>'+options;

                        // remove present options
                        $('option', filterElement).remove();

                        // add new options
                        $(filterElement).append($(options));

                        break;
                    }

                    // @todo
                    case 'SELECTMULTIPLE': {
                        break;
                    }
                }
            });
        },

        
        // helper for removing hover and focus
        _removeHoverAndFocus: function(obj) {
            if($(obj).hasClass('ui-state-hover')){
                $(obj).removeClass('ui-state-hover');
            }

            if($(obj).hasClass('ui-state-focus')){
                $(obj).removeClass('ui-state-focus');
            }
        },

        // helper to en/disable a button
        _toggleButtonStatus: function(obj, enable) {
            if(enable===true){
                if($(obj).button('option','disabled')){
                    $(obj).button('enable');
                }
            }else{
                if(!$(obj).button('option','disabled')){
                    $(obj).button('disable');
                }
            }
        },

        destroy: function() {
            this._xdatatable.remove();
            $.Widget.prototype.destroy.apply(this, arguments);
        }
    });

    $.extend($.ui.xdatatable, {
        version: '@VERSION'
    });
})(jQuery);