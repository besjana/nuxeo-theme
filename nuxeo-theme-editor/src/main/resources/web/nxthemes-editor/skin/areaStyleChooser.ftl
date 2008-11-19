
<div class="nxthemesToolbox" id="nxthemesAreaStyleChooser">

<div class="title">
<img class="close" onclick="javascript:NXThemesEditor.closeAreaStyleChooser()"
     src="/skin/nxthemes-editor/img/close-button.png" width="14" height="14" alt="" />

Style chooser - ${style_category}</div>

<div class="header">PRESETS:
  <div>
    <select id="areaStyleGroupName" onchange="NXThemesEditor.setPresetGroup(this)">
      <#list preset_groups as preset_group>
        <#if selected_preset_group == preset_group>
          <option value="${preset_group}" selected="selected">${preset_group}</option>
        <#else>
          <option value="${preset_group}">${preset_group}</option>
        </#if>
      </#list>
    </select>
  </div>
</div>

<div class="frame">
  <#if presets_for_selected_group>
    <#list presets_for_selected_group as preset_info>
      <div class="selection" onclick="NXThemesEditor.updateAreaStyle('&quot;${preset_info.name}&quot;')">
          ${preset_info.preview}
      </div>
    </#list>
  <#else>
    <div class="selection" style="padding: 10px 5px; font-style: italic; font-weight: 1.2em;"
      onclick="NXThemesEditor.updateAreaStyle(null)">
      No style
    </div>
  </#if>
</div>

<div class="footer">
&#32;
</div>

</div>
