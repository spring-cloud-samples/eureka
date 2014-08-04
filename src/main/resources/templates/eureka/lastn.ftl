<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
    <base href="${basePath}">
    
    <title>Eureka - Last N events</title>
    <link rel="stylesheet" type="text/css" href="./css/main.css">
    <link type="text/css" href="./css/jquery-ui-1.7.2.custom.css" rel="Stylesheet" />  
    <script type="text/javascript" src="./js/jquery-1.3.2.js" ></script>
    <script type="text/javascript" src="./js/jquery-ui-1.7.2.custom.min.js"></script>
    <script type="text/javascript" >
       $(document).ready(function() {
           $('table.stripeable tr:odd').addClass('odd');
           $('table.stripeable tr:even').addClass('even');
           $("#tabs").tabs();
           });
    </script>
  </head>
  
  <body id="three">
    <#include "header.ftl">
    <#include "navbar.ftl">
    <div id="content">
	<div id="tabs">
	<ul>
	    <li><a href="#tabs-1">Last 1000 canceled leases</a></li>
	    <li><a href="#tabs-2">Last 1000 newly registered leases</a></li>
	</ul>
    <div id="tabs-1">
      <table id='lastNCanceled' class="stripeable">
      <tr><th>Timestamp</th><th>Lease</th></tr>
          <#list lastNCanceled as entry>
              <tr><td>${entry.date?datetime}</td><td>${entry.id}</td></tr>
          </#list>
      </table>
    </div>
    <div id="tabs-2">
      <table id='lastNRegistered' class="stripeable">
      <tr><th>Timestamp</th><th>Lease</th></tr>
      <#list lastNRegistered as entry>
          <tr><td>${entry.date?datetime}</td><td>${entry.id}</td></tr>
      </#list>
      </table>
    </div>
  </div>
  </div>
  </body>
</html>
