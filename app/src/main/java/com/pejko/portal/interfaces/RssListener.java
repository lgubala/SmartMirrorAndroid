package com.pejko.portal.interfaces;

import com.pejko.portal.entity.ModelRss;

import java.util.List;

public interface RssListener {

    void downloadFinished(List<ModelRss> rssItems);

}
