package ru.omickron.service;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.omickron.model.Rss;
import ru.omickron.model.RssChannel;
import ru.omickron.model.RssItem;

@Component
@Slf4j
public class VkService extends AbstractLoadingService {
    private static final String LINK_VK = "http://vk.com/";
    private static final String LINK_VK_API = "https://api.vk.com/method/";
    private static final String VK_API_VERSION = "5.52";
    private static final String LINK_VK_GET_POSTS =
            LINK_VK_API + "wall.get?v=" + VK_API_VERSION + "&count=30&owner_id=-";
    private static final String LINK_VK_GET_GROUP =
            LINK_VK_API + "groups.getById?v=" + VK_API_VERSION + "&fields=description&group_id=";
    private static final String LINK_VK_POST = "<![CDATA[" + LINK_VK + "wall-%1$s?own=1&w=wall-%1$s_%2$s]]>";
    private static final String BREAK = "<br>";
    private static final String P_TEXT = "text";
    private static final String P_COPY_TEXT = "copy_text";
    private static final String P_IS_CLOSED = "is_closed";
    private static final String P_NAME = "name";
    private static final String P_DESCRIPTION = "description";
    private static final String P_ID = "id";
    private static final String P_DATE = "date";
    private static final String P_ATTACHMENTS = "attachments";
    private static final String ATTACHMENT_TYPE_PHOTO = "photo";
    private static final String P_TYPE = "type";
    private static final String P_PHOTO = "photo";
    private static final String P_PHOTO_604 = "photo_604";
    private static final String P_PHOTO_807 = "photo_807";
    private static final String P_RESPONSE = "response";
    private static final String P_PHOTO_1280 = "photo_1280";
    private static final String TEMPLATE_LINK = "\\[([^\\|]+)\\|([^\\]]+)\\]";
    private static final String TEMPLATE_LINK_REPLACE = "<a href=$1>$2</a>";

    @Cacheable(value = "rss")
    public Rss getRss( String id ) {
        log.debug( "Generating channel {} ", id );
        return new Rss( getChannel( id ) );
    }

    private RssChannel getChannel( String id ) {
        String title = null;
        String link = null;
        String description = null;
        List<RssItem> items = Lists.newArrayList();
        try {
            JSONObject groupJson = getGroupJson( id );
            if (groupJson != null) {
                int closed = groupJson.getInt( P_IS_CLOSED );
                title = groupJson.getString( P_NAME );
                if (closed == 0) {
                    link = LINK_VK + id;
                    description = groupJson.getString( P_DESCRIPTION );
                    String groupId = String.valueOf( groupJson.get( P_ID ) );

                    JSONArray itemsJson = getItemsJson( groupId );
                    for (int i = 0; i < itemsJson.length(); i++) {
                        JSONObject itemJson = itemsJson.getJSONObject( i );

                        String itemId = String.valueOf( itemJson.get( P_ID ) );
                        String itemLink = String.format( LINK_VK_POST, groupId, itemId );
                        String itemDescription = itemJson.getString( P_TEXT );
                        if (itemJson.has( P_COPY_TEXT )) {
                            itemDescription = itemJson.getString( P_COPY_TEXT ) + BREAK + itemDescription;
                        }
                        itemDescription = itemDescription.replace( "\n", BREAK );
                        List<String> photos = getPhotos( itemJson );
                        if (!photos.isEmpty()) {
                            itemDescription = getPhotoLink( photos.get( 0 ) ) + BREAK + itemDescription;
                            photos.remove( 0 );
                        }
                        if (!photos.isEmpty()) {
                            for (String photo : photos) {
                                itemDescription = itemDescription + BREAK + getPhotoLink( photo );
                            }
                        }
                        itemDescription = itemDescription.replaceAll( TEMPLATE_LINK, TEMPLATE_LINK_REPLACE );
                        itemDescription = String.format( "<![CDATA[%s]]>", itemDescription );
                        long pubDate = itemJson.getInt( P_DATE );
                        RssItem rssItem = new RssItem( title, itemLink, itemDescription, pubDate );

                        items.add( rssItem );
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RssChannel channel = new RssChannel( title, link, description );
        channel.getItems().addAll( items );
        return channel;
    }

    private String getPhotoLink( String photo ) {
        return String.format( "<a href=\"%1$s\"><img src=\"%1$s\"/></a>", photo );
    }

    private List<String> getPhotos( JSONObject itemJson ) throws JSONException {
        List<String> result = Lists.newArrayList();
        if (itemJson.has( P_ATTACHMENTS )) {
            JSONArray attachments = itemJson.getJSONArray( P_ATTACHMENTS );
            for (int i = 0; i < attachments.length(); i++) {
                JSONObject attachment = attachments.getJSONObject( i );
                if (attachment.getString( P_TYPE ).equalsIgnoreCase( ATTACHMENT_TYPE_PHOTO )) {
                    JSONObject photo = attachment.getJSONObject( P_PHOTO );
                    if (photo.has( P_PHOTO_604 )) {
                        result.add( photo.getString( P_PHOTO_604 ) );
                    } else if (photo.has( P_PHOTO_807 )) {
                        result.add( photo.getString( P_PHOTO_807 ) );
                    } else if (photo.has( P_PHOTO_1280 )) {
                        result.add( photo.getString( P_PHOTO_1280 ) );
                    }
                }
            }
        }
        return result;
    }

    private JSONObject getGroupJson( String id ) throws JSONException {
        String responce = client.resource( LINK_VK_GET_GROUP + id ).get( String.class );
        JSONObject object = new JSONObject( responce );
        if (object.has( P_RESPONSE )) {
            return object.getJSONArray( P_RESPONSE ).getJSONObject( 0 );
        }
        return null;
    }

    private JSONArray getItemsJson( String id ) throws JSONException {
        String responce = client.resource( LINK_VK_GET_POSTS + id ).get( String.class );
        return new JSONObject( responce ).getJSONObject( "response" ).getJSONArray( "items" );
    }
}
