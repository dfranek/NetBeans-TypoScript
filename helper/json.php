<?php
/**
 * Converts the XML-Documentation from t3editor to JSON and modfifies it for use in the plugin
 */
$xml = simplexml_load_file("tsref.xml");
$types = array();
foreach ($xml->type as $type) {
	$typeId = (string)$type['id'];
	if($typeId == 'GB_TEXT') {
		$typeId = 'GIFBUILDER_TEXT';
	}
	if($typeId == 'GB_IMAGE') {
		$typeId = 'GIFBUILDER_IMAGE';
	}
	
	$types[$typeId]['extendsType'] = (string)$type['extends'];
	foreach ($type->property as $property) {
		$types[$typeId]['properties'][(string)$property['name']] = array(
			'type' => (string)$property['type'],
			'defaultValue' => (string)$property->default,
			'description' => nl2br((string)$property->description),
		);
	}
}
echo "<pre>";
print_r($types);
echo "</pre>";
file_put_contents('tsref.json', json_encode($types));

?>