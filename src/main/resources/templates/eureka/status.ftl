<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
    <base href="${basePath}">
    
    <title>Eureka</title>
    <link rel="stylesheet" type="text/css" href="./css/main.css">
    <script type="text/javascript" src="./js/jquery-1.3.2.js" ></script>
    <script type="text/javascript" >
       $(document).ready(function() {
           $('table.stripeable tr:odd').addClass('odd');
           $('table.stripeable tr:even').addClass('even');
           });
    </script>
  </head>
  
  <body id="one">
    <#include "header.ftl">
    <#include "navbar.ftl">
    <div id="content">
      <div class="sectionTitle">Instances currently registered with Eureka</div>
        <table id='instances' class="stripeable">
          <tr><th>Application</th><th>AMIs</th><th>Availability Zones</th><th>Status</th></tr>
          <#list apps as app>
            <tr><td><b>${app.name}</b></td>
                <td>
                  <#list app.amiCounts as amiCount>
                      <b>${amiCount.key}</b> (${amiCount.value})<#if amiCount_has_next>,</#if>
                  </#list>
                </td>
                <td>
                  <#list app.zoneCounts as zoneCount>
                       <b>${zoneCount.key}</b> (${zoneCount.value})<#if zoneCount_has_next>,</#if>
                  </#list>
                </td>
                <td>
                    <#list app.instanceInfos as instanceInfo>
                        <#if instanceInfo.isNotUp><font color=red size=+1><b></#if>
                        <b>${instanceInfo.status}</b> (${instanceInfo.instances?size}) -
                        <#if instanceInfo.isNotUp></b></font></#if>
                        <#list instanceInfo.instances as instance>
                            <#if instance.isHref>
                                <a href="${instance.url}">${instance.id}</a>
                            <#else>
                                ${instance.id}
                            </#if><#if instance_has_next>,</#if>
                        </#list>
                    </#list>
                </td>
            </tr>
          </#list>
           </table>
      </div>
      <div>
      <div class="sectionTitle">General Info</div>
      <table id='generalInfo' class="stripeable">
          <tr><th>Name</th><th>Value</th></tr>
          <#list statusInfo.generalStats?keys as stat>
              <tr>
              <td>${stat}</td><td>${statusInfo.generalStats[stat]!""}</td>
              </tr>
          </#list>
          <#list statusInfo.applicationStats?keys as stat>
              <tr>
                  <td>${stat}</td><td>${statusInfo.applicationStats[stat]!""}</td>
              </tr>
          </#list>
           </table>
      </div>
      <div>
      <div class="sectionTitle">Instance Info</div>
        <table id='instanceInfo' class="stripeable">
          <tr><th>Name</th><th>Value</th></tr>
        <#list instanceInfo?keys as key>
            <tr>
                <td>${key}</td><td>${instanceInfo[key]!""}</td>
            </tr>
        </#list>
        </table>
    </div>

  </body>
</html>
