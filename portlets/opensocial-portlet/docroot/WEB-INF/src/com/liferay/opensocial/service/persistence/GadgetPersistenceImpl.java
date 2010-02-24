/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.opensocial.service.persistence;

import com.liferay.opensocial.NoSuchGadgetException;
import com.liferay.opensocial.model.Gadget;
import com.liferay.opensocial.model.impl.GadgetImpl;
import com.liferay.opensocial.model.impl.GadgetModelImpl;

import com.liferay.portal.NoSuchModelException;
import com.liferay.portal.kernel.annotation.BeanReference;
import com.liferay.portal.kernel.cache.CacheRegistry;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.BatchSessionUtil;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <a href="GadgetPersistenceImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class GadgetPersistenceImpl extends BasePersistenceImpl<Gadget>
	implements GadgetPersistence {
	public static final String FINDER_CLASS_NAME_ENTITY = GadgetImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST = FINDER_CLASS_NAME_ENTITY +
		".List";
	public static final FinderPath FINDER_PATH_FIND_BY_COMPANYID = new FinderPath(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByCompanyId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_COMPANYID = new FinderPath(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByCompanyId",
			new String[] {
				Long.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_COMPANYID = new FinderPath(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByCompanyId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_ALL = new FinderPath(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countAll", new String[0]);

	public void cacheResult(Gadget gadget) {
		EntityCacheUtil.putResult(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetImpl.class, gadget.getPrimaryKey(), gadget);
	}

	public void cacheResult(List<Gadget> gadgets) {
		for (Gadget gadget : gadgets) {
			if (EntityCacheUtil.getResult(
						GadgetModelImpl.ENTITY_CACHE_ENABLED, GadgetImpl.class,
						gadget.getPrimaryKey(), this) == null) {
				cacheResult(gadget);
			}
		}
	}

	public void clearCache() {
		CacheRegistry.clear(GadgetImpl.class.getName());
		EntityCacheUtil.clearCache(GadgetImpl.class.getName());
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);
	}

	public Gadget create(long gadgetId) {
		Gadget gadget = new GadgetImpl();

		gadget.setNew(true);
		gadget.setPrimaryKey(gadgetId);

		return gadget;
	}

	public Gadget remove(Serializable primaryKey)
		throws NoSuchModelException, SystemException {
		return remove(((Long)primaryKey).longValue());
	}

	public Gadget remove(long gadgetId)
		throws NoSuchGadgetException, SystemException {
		Session session = null;

		try {
			session = openSession();

			Gadget gadget = (Gadget)session.get(GadgetImpl.class,
					new Long(gadgetId));

			if (gadget == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + gadgetId);
				}

				throw new NoSuchGadgetException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					gadgetId);
			}

			return remove(gadget);
		}
		catch (NoSuchGadgetException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public Gadget remove(Gadget gadget) throws SystemException {
		for (ModelListener<Gadget> listener : listeners) {
			listener.onBeforeRemove(gadget);
		}

		gadget = removeImpl(gadget);

		for (ModelListener<Gadget> listener : listeners) {
			listener.onAfterRemove(gadget);
		}

		return gadget;
	}

	protected Gadget removeImpl(Gadget gadget) throws SystemException {
		gadget = toUnwrappedModel(gadget);

		Session session = null;

		try {
			session = openSession();

			if (gadget.isCachedModel() || BatchSessionUtil.isEnabled()) {
				Object staleObject = session.get(GadgetImpl.class,
						gadget.getPrimaryKeyObj());

				if (staleObject != null) {
					session.evict(staleObject);
				}
			}

			session.delete(gadget);

			session.flush();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);

		EntityCacheUtil.removeResult(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetImpl.class, gadget.getPrimaryKey());

		return gadget;
	}

	public Gadget updateImpl(com.liferay.opensocial.model.Gadget gadget,
		boolean merge) throws SystemException {
		gadget = toUnwrappedModel(gadget);

		Session session = null;

		try {
			session = openSession();

			BatchSessionUtil.update(session, gadget, merge);

			gadget.setNew(false);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);

		EntityCacheUtil.putResult(GadgetModelImpl.ENTITY_CACHE_ENABLED,
			GadgetImpl.class, gadget.getPrimaryKey(), gadget);

		return gadget;
	}

	protected Gadget toUnwrappedModel(Gadget gadget) {
		if (gadget instanceof GadgetImpl) {
			return gadget;
		}

		GadgetImpl gadgetImpl = new GadgetImpl();

		gadgetImpl.setNew(gadget.isNew());
		gadgetImpl.setPrimaryKey(gadget.getPrimaryKey());

		gadgetImpl.setGadgetId(gadget.getGadgetId());
		gadgetImpl.setCompanyId(gadget.getCompanyId());
		gadgetImpl.setCreateDate(gadget.getCreateDate());
		gadgetImpl.setModifiedDate(gadget.getModifiedDate());
		gadgetImpl.setName(gadget.getName());
		gadgetImpl.setUrl(gadget.getUrl());
		gadgetImpl.setXml(gadget.getXml());

		return gadgetImpl;
	}

	public Gadget findByPrimaryKey(Serializable primaryKey)
		throws NoSuchModelException, SystemException {
		return findByPrimaryKey(((Long)primaryKey).longValue());
	}

	public Gadget findByPrimaryKey(long gadgetId)
		throws NoSuchGadgetException, SystemException {
		Gadget gadget = fetchByPrimaryKey(gadgetId);

		if (gadget == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + gadgetId);
			}

			throw new NoSuchGadgetException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				gadgetId);
		}

		return gadget;
	}

	public Gadget fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		return fetchByPrimaryKey(((Long)primaryKey).longValue());
	}

	public Gadget fetchByPrimaryKey(long gadgetId) throws SystemException {
		Gadget gadget = (Gadget)EntityCacheUtil.getResult(GadgetModelImpl.ENTITY_CACHE_ENABLED,
				GadgetImpl.class, gadgetId, this);

		if (gadget == null) {
			Session session = null;

			try {
				session = openSession();

				gadget = (Gadget)session.get(GadgetImpl.class,
						new Long(gadgetId));
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (gadget != null) {
					cacheResult(gadget);
				}

				closeSession(session);
			}
		}

		return gadget;
	}

	public List<Gadget> findByCompanyId(long companyId)
		throws SystemException {
		Object[] finderArgs = new Object[] { new Long(companyId) };

		List<Gadget> list = (List<Gadget>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_COMPANYID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_SELECT_GADGET_WHERE);

				query.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				query.append(GadgetModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(companyId);

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<Gadget>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_COMPANYID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<Gadget> findByCompanyId(long companyId, int start, int end)
		throws SystemException {
		return findByCompanyId(companyId, start, end, null);
	}

	public List<Gadget> findByCompanyId(long companyId, int start, int end,
		OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(companyId),
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<Gadget> list = (List<Gadget>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_COMPANYID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(3 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(3);
				}

				query.append(_SQL_SELECT_GADGET_WHERE);

				query.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(GadgetModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(companyId);

				list = (List<Gadget>)QueryUtil.list(q, getDialect(), start, end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<Gadget>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_COMPANYID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public Gadget findByCompanyId_First(long companyId, OrderByComparator obc)
		throws NoSuchGadgetException, SystemException {
		List<Gadget> list = findByCompanyId(companyId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("companyId=");
			msg.append(companyId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchGadgetException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public Gadget findByCompanyId_Last(long companyId, OrderByComparator obc)
		throws NoSuchGadgetException, SystemException {
		int count = countByCompanyId(companyId);

		List<Gadget> list = findByCompanyId(companyId, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("companyId=");
			msg.append(companyId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchGadgetException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public Gadget[] findByCompanyId_PrevAndNext(long gadgetId, long companyId,
		OrderByComparator obc) throws NoSuchGadgetException, SystemException {
		Gadget gadget = findByPrimaryKey(gadgetId);

		int count = countByCompanyId(companyId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(3 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(3);
			}

			query.append(_SQL_SELECT_GADGET_WHERE);

			query.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(GadgetModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(companyId);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc, gadget);

			Gadget[] array = new GadgetImpl[3];

			array[0] = (Gadget)objArray[0];
			array[1] = (Gadget)objArray[1];
			array[2] = (Gadget)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Object> findWithDynamicQuery(DynamicQuery dynamicQuery)
		throws SystemException {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Object> findWithDynamicQuery(DynamicQuery dynamicQuery,
		int start, int end) throws SystemException {
		Session session = null;

		try {
			session = openSession();

			dynamicQuery.setLimit(start, end);

			dynamicQuery.compile(session);

			return dynamicQuery.list();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<Gadget> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public List<Gadget> findAll(int start, int end) throws SystemException {
		return findAll(start, end, null);
	}

	public List<Gadget> findAll(int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<Gadget> list = (List<Gadget>)FinderCacheUtil.getResult(FINDER_PATH_FIND_ALL,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;
				String sql = null;

				if (obc != null) {
					query = new StringBundler(2 +
							(obc.getOrderByFields().length * 3));

					query.append(_SQL_SELECT_GADGET);

					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);

					sql = query.toString();
				}

				else {
					sql = _SQL_SELECT_GADGET.concat(GadgetModelImpl.ORDER_BY_JPQL);
				}

				Query q = session.createQuery(sql);

				if (obc == null) {
					list = (List<Gadget>)QueryUtil.list(q, getDialect(), start,
							end, false);

					Collections.sort(list);
				}
				else {
					list = (List<Gadget>)QueryUtil.list(q, getDialect(), start,
							end);
				}
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<Gadget>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_ALL, finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public void removeByCompanyId(long companyId) throws SystemException {
		for (Gadget gadget : findByCompanyId(companyId)) {
			remove(gadget);
		}
	}

	public void removeAll() throws SystemException {
		for (Gadget gadget : findAll()) {
			remove(gadget);
		}
	}

	public int countByCompanyId(long companyId) throws SystemException {
		Object[] finderArgs = new Object[] { new Long(companyId) };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_COMPANYID,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(2);

				query.append(_SQL_COUNT_GADGET_WHERE);

				query.append(_FINDER_COLUMN_COMPANYID_COMPANYID_2);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(companyId);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_COMPANYID,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countAll() throws SystemException {
		Object[] finderArgs = new Object[0];

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_ALL,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query q = session.createQuery(_SQL_COUNT_GADGET);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_ALL, finderArgs,
					count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public void afterPropertiesSet() {
		String[] listenerClassNames = StringUtil.split(GetterUtil.getString(
					com.liferay.util.service.ServiceProps.get(
						"value.object.listener.com.liferay.opensocial.model.Gadget")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<Gadget>> listenersList = new ArrayList<ModelListener<Gadget>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<Gadget>)Class.forName(
							listenerClassName).newInstance());
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	@BeanReference(name = "com.liferay.opensocial.service.persistence.GadgetPersistence")
	protected com.liferay.opensocial.service.persistence.GadgetPersistence gadgetPersistence;
	@BeanReference(name = "com.liferay.portal.service.persistence.ResourcePersistence")
	protected com.liferay.portal.service.persistence.ResourcePersistence resourcePersistence;
	@BeanReference(name = "com.liferay.portal.service.persistence.UserPersistence")
	protected com.liferay.portal.service.persistence.UserPersistence userPersistence;
	private static final String _SQL_SELECT_GADGET = "SELECT gadget FROM Gadget gadget";
	private static final String _SQL_SELECT_GADGET_WHERE = "SELECT gadget FROM Gadget gadget WHERE ";
	private static final String _SQL_COUNT_GADGET = "SELECT COUNT(gadget) FROM Gadget gadget";
	private static final String _SQL_COUNT_GADGET_WHERE = "SELECT COUNT(gadget) FROM Gadget gadget WHERE ";
	private static final String _FINDER_COLUMN_COMPANYID_COMPANYID_2 = "gadget.companyId = ?";
	private static final String _ORDER_BY_ENTITY_ALIAS = "gadget.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No Gadget exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No Gadget exists with the key {";
	private static Log _log = LogFactoryUtil.getLog(GadgetPersistenceImpl.class);
}