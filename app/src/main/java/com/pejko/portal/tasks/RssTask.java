package com.pejko.portal.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.casperise.common.utils.NetworkUtils;
import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;
import com.pejko.portal.entity.ModelRss;
import com.pejko.portal.interfaces.RssListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

public class RssTask extends AsyncTask<String, Void, List<ModelRss>>
{

    private String url;
    private RssListener listener;
    private Context context;

    public RssTask(Context context, String url, RssListener listener)
    {
        this.context = context;
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected List<ModelRss> doInBackground(String... params)
    {
        List<ModelRss> array = null;
        if (NetworkUtils.isNetworkAvailable(context)) {
            InputStream inputStream = null;
            try {
                URL link = new URL(url);
                inputStream = (link).openConnection().getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Feed feed = null;
            try {
                feed = EarlParser.parseOrThrow(inputStream, 0);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
            List<Item> itemList = (List<Item>) feed.getItems();

            array = new ArrayList<>();
            for (Item item : itemList) {
                ModelRss model = new ModelRss(item);
                Document doc = Jsoup.parse(item.getDescription());
                for (Element e : doc.select("img")) {
                    model.setImageLink(e.attr("src"));
                    break;
                }
                model.setDescription(cleanText(model.getDescription()));
                array.add(model);
            }
        }

        return array;
    }

    private String cleanText(String text) {
        Document doc = Jsoup.parse(text);
        doc.select("src").remove();
        //doc.select("href").remove();
        return Jsoup.clean(doc.body().html(), Whitelist.basic());
    }

    @Override
    protected void onPostExecute(List<ModelRss> rssItems) {
        if (listener != null) {
            listener.downloadFinished(rssItems);
        }
    }
}