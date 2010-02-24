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

package com.liferay.socialcoding.service.persistence;

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
import com.liferay.portal.kernel.util.CalendarUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.service.persistence.BatchSessionUtil;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;

import com.liferay.socialcoding.NoSuchJIRAIssueException;
import com.liferay.socialcoding.model.JIRAIssue;
import com.liferay.socialcoding.model.impl.JIRAIssueImpl;
import com.liferay.socialcoding.model.impl.JIRAIssueModelImpl;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <a href="JIRAIssuePersistenceImpl.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 */
public class JIRAIssuePersistenceImpl extends BasePersistenceImpl<JIRAIssue>
	implements JIRAIssuePersistence {
	public static final String FINDER_CLASS_NAME_ENTITY = JIRAIssueImpl.class.getName();
	public static final String FINDER_CLASS_NAME_LIST = FINDER_CLASS_NAME_ENTITY +
		".List";
	public static final FinderPath FINDER_PATH_FIND_BY_PROJECTID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByProjectId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_PROJECTID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByProjectId",
			new String[] {
				Long.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_PROJECTID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByProjectId", new String[] { Long.class.getName() });
	public static final FinderPath FINDER_PATH_FETCH_BY_KEY = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_ENTITY,
			"fetchByKey", new String[] { String.class.getName() });
	public static final FinderPath FINDER_PATH_COUNT_BY_KEY = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByKey", new String[] { String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_REPORTERJIRAUSERID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByReporterJiraUserId", new String[] { String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_REPORTERJIRAUSERID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByReporterJiraUserId",
			new String[] {
				String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_REPORTERJIRAUSERID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByReporterJiraUserId", new String[] { String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_ASSIGNEEJIRAUSERID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByAssigneeJiraUserId", new String[] { String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_ASSIGNEEJIRAUSERID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByAssigneeJiraUserId",
			new String[] {
				String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_ASSIGNEEJIRAUSERID = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByAssigneeJiraUserId", new String[] { String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_MD_P = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByMD_P",
			new String[] { Date.class.getName(), Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_MD_P = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByMD_P",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_MD_P = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByMD_P",
			new String[] { Date.class.getName(), Long.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_P_RJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_RJUI",
			new String[] { Long.class.getName(), String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_P_RJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_RJUI",
			new String[] {
				Long.class.getName(), String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_P_RJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByP_RJUI",
			new String[] { Long.class.getName(), String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_P_AJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_AJUI",
			new String[] { Long.class.getName(), String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_P_AJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_AJUI",
			new String[] {
				Long.class.getName(), String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_P_AJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByP_AJUI",
			new String[] { Long.class.getName(), String.class.getName() });
	public static final FinderPath FINDER_PATH_FIND_BY_MD_P_RJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByMD_P_RJUI",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_MD_P_RJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByMD_P_RJUI",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_MD_P_RJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByMD_P_RJUI",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_MD_P_AJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByMD_P_AJUI",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_MD_P_AJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByMD_P_AJUI",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_MD_P_AJUI = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByMD_P_AJUI",
			new String[] {
				Date.class.getName(), Long.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_P_RJUI_S = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_RJUI_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_P_RJUI_S = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_RJUI_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_P_RJUI_S = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByP_RJUI_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_P_AJUI_S = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_AJUI_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_BY_OBC_P_AJUI_S = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findByP_AJUI_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName(),
				
			"java.lang.Integer", "java.lang.Integer",
				"com.liferay.portal.kernel.util.OrderByComparator"
			});
	public static final FinderPath FINDER_PATH_COUNT_BY_P_AJUI_S = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countByP_AJUI_S",
			new String[] {
				Long.class.getName(), String.class.getName(),
				String.class.getName()
			});
	public static final FinderPath FINDER_PATH_FIND_ALL = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"findAll", new String[0]);
	public static final FinderPath FINDER_PATH_COUNT_ALL = new FinderPath(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueModelImpl.FINDER_CACHE_ENABLED, FINDER_CLASS_NAME_LIST,
			"countAll", new String[0]);

	public void cacheResult(JIRAIssue jiraIssue) {
		EntityCacheUtil.putResult(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueImpl.class, jiraIssue.getPrimaryKey(), jiraIssue);

		FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_KEY,
			new Object[] { jiraIssue.getKey() }, jiraIssue);
	}

	public void cacheResult(List<JIRAIssue> jiraIssues) {
		for (JIRAIssue jiraIssue : jiraIssues) {
			if (EntityCacheUtil.getResult(
						JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
						JIRAIssueImpl.class, jiraIssue.getPrimaryKey(), this) == null) {
				cacheResult(jiraIssue);
			}
		}
	}

	public void clearCache() {
		CacheRegistry.clear(JIRAIssueImpl.class.getName());
		EntityCacheUtil.clearCache(JIRAIssueImpl.class.getName());
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_ENTITY);
		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);
	}

	public JIRAIssue create(long jiraIssueId) {
		JIRAIssue jiraIssue = new JIRAIssueImpl();

		jiraIssue.setNew(true);
		jiraIssue.setPrimaryKey(jiraIssueId);

		return jiraIssue;
	}

	public JIRAIssue remove(Serializable primaryKey)
		throws NoSuchModelException, SystemException {
		return remove(((Long)primaryKey).longValue());
	}

	public JIRAIssue remove(long jiraIssueId)
		throws NoSuchJIRAIssueException, SystemException {
		Session session = null;

		try {
			session = openSession();

			JIRAIssue jiraIssue = (JIRAIssue)session.get(JIRAIssueImpl.class,
					new Long(jiraIssueId));

			if (jiraIssue == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + jiraIssueId);
				}

				throw new NoSuchJIRAIssueException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
					jiraIssueId);
			}

			return remove(jiraIssue);
		}
		catch (NoSuchJIRAIssueException nsee) {
			throw nsee;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public JIRAIssue remove(JIRAIssue jiraIssue) throws SystemException {
		for (ModelListener<JIRAIssue> listener : listeners) {
			listener.onBeforeRemove(jiraIssue);
		}

		jiraIssue = removeImpl(jiraIssue);

		for (ModelListener<JIRAIssue> listener : listeners) {
			listener.onAfterRemove(jiraIssue);
		}

		return jiraIssue;
	}

	protected JIRAIssue removeImpl(JIRAIssue jiraIssue)
		throws SystemException {
		jiraIssue = toUnwrappedModel(jiraIssue);

		Session session = null;

		try {
			session = openSession();

			if (jiraIssue.isCachedModel() || BatchSessionUtil.isEnabled()) {
				Object staleObject = session.get(JIRAIssueImpl.class,
						jiraIssue.getPrimaryKeyObj());

				if (staleObject != null) {
					session.evict(staleObject);
				}
			}

			session.delete(jiraIssue);

			session.flush();
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);

		JIRAIssueModelImpl jiraIssueModelImpl = (JIRAIssueModelImpl)jiraIssue;

		FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_KEY,
			new Object[] { jiraIssueModelImpl.getOriginalKey() });

		EntityCacheUtil.removeResult(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueImpl.class, jiraIssue.getPrimaryKey());

		return jiraIssue;
	}

	public JIRAIssue updateImpl(
		com.liferay.socialcoding.model.JIRAIssue jiraIssue, boolean merge)
		throws SystemException {
		jiraIssue = toUnwrappedModel(jiraIssue);

		boolean isNew = jiraIssue.isNew();

		JIRAIssueModelImpl jiraIssueModelImpl = (JIRAIssueModelImpl)jiraIssue;

		Session session = null;

		try {
			session = openSession();

			BatchSessionUtil.update(session, jiraIssue, merge);

			jiraIssue.setNew(false);
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}

		FinderCacheUtil.clearCache(FINDER_CLASS_NAME_LIST);

		EntityCacheUtil.putResult(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
			JIRAIssueImpl.class, jiraIssue.getPrimaryKey(), jiraIssue);

		if (!isNew &&
				(!Validator.equals(jiraIssue.getKey(),
					jiraIssueModelImpl.getOriginalKey()))) {
			FinderCacheUtil.removeResult(FINDER_PATH_FETCH_BY_KEY,
				new Object[] { jiraIssueModelImpl.getOriginalKey() });
		}

		if (isNew ||
				(!Validator.equals(jiraIssue.getKey(),
					jiraIssueModelImpl.getOriginalKey()))) {
			FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_KEY,
				new Object[] { jiraIssue.getKey() }, jiraIssue);
		}

		return jiraIssue;
	}

	protected JIRAIssue toUnwrappedModel(JIRAIssue jiraIssue) {
		if (jiraIssue instanceof JIRAIssueImpl) {
			return jiraIssue;
		}

		JIRAIssueImpl jiraIssueImpl = new JIRAIssueImpl();

		jiraIssueImpl.setNew(jiraIssue.isNew());
		jiraIssueImpl.setPrimaryKey(jiraIssue.getPrimaryKey());

		jiraIssueImpl.setJiraIssueId(jiraIssue.getJiraIssueId());
		jiraIssueImpl.setCreateDate(jiraIssue.getCreateDate());
		jiraIssueImpl.setModifiedDate(jiraIssue.getModifiedDate());
		jiraIssueImpl.setProjectId(jiraIssue.getProjectId());
		jiraIssueImpl.setKey(jiraIssue.getKey());
		jiraIssueImpl.setSummary(jiraIssue.getSummary());
		jiraIssueImpl.setDescription(jiraIssue.getDescription());
		jiraIssueImpl.setReporterJiraUserId(jiraIssue.getReporterJiraUserId());
		jiraIssueImpl.setAssigneeJiraUserId(jiraIssue.getAssigneeJiraUserId());
		jiraIssueImpl.setResolution(jiraIssue.getResolution());
		jiraIssueImpl.setStatus(jiraIssue.getStatus());

		return jiraIssueImpl;
	}

	public JIRAIssue findByPrimaryKey(Serializable primaryKey)
		throws NoSuchModelException, SystemException {
		return findByPrimaryKey(((Long)primaryKey).longValue());
	}

	public JIRAIssue findByPrimaryKey(long jiraIssueId)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = fetchByPrimaryKey(jiraIssueId);

		if (jiraIssue == null) {
			if (_log.isWarnEnabled()) {
				_log.warn(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + jiraIssueId);
			}

			throw new NoSuchJIRAIssueException(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY +
				jiraIssueId);
		}

		return jiraIssue;
	}

	public JIRAIssue fetchByPrimaryKey(Serializable primaryKey)
		throws SystemException {
		return fetchByPrimaryKey(((Long)primaryKey).longValue());
	}

	public JIRAIssue fetchByPrimaryKey(long jiraIssueId)
		throws SystemException {
		JIRAIssue jiraIssue = (JIRAIssue)EntityCacheUtil.getResult(JIRAIssueModelImpl.ENTITY_CACHE_ENABLED,
				JIRAIssueImpl.class, jiraIssueId, this);

		if (jiraIssue == null) {
			Session session = null;

			try {
				session = openSession();

				jiraIssue = (JIRAIssue)session.get(JIRAIssueImpl.class,
						new Long(jiraIssueId));
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (jiraIssue != null) {
					cacheResult(jiraIssue);
				}

				closeSession(session);
			}
		}

		return jiraIssue;
	}

	public List<JIRAIssue> findByProjectId(long projectId)
		throws SystemException {
		Object[] finderArgs = new Object[] { new Long(projectId) };

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_PROJECTID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_PROJECTID_PROJECTID_2);

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_PROJECTID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByProjectId(long projectId, int start, int end)
		throws SystemException {
		return findByProjectId(projectId, start, end, null);
	}

	public List<JIRAIssue> findByProjectId(long projectId, int start, int end,
		OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_PROJECTID,
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

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_PROJECTID_PROJECTID_2);

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_PROJECTID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByProjectId_First(long projectId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByProjectId(projectId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByProjectId_Last(long projectId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByProjectId(projectId);

		List<JIRAIssue> list = findByProjectId(projectId, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByProjectId_PrevAndNext(long jiraIssueId,
		long projectId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByProjectId(projectId);

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

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			query.append(_FINDER_COLUMN_PROJECTID_PROJECTID_2);

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(projectId);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public JIRAIssue findByKey(String key)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = fetchByKey(key);

		if (jiraIssue == null) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("key=");
			msg.append(key);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			if (_log.isWarnEnabled()) {
				_log.warn(msg.toString());
			}

			throw new NoSuchJIRAIssueException(msg.toString());
		}

		return jiraIssue;
	}

	public JIRAIssue fetchByKey(String key) throws SystemException {
		return fetchByKey(key, true);
	}

	public JIRAIssue fetchByKey(String key, boolean retrieveFromCache)
		throws SystemException {
		Object[] finderArgs = new Object[] { key };

		Object result = null;

		if (retrieveFromCache) {
			result = FinderCacheUtil.getResult(FINDER_PATH_FETCH_BY_KEY,
					finderArgs, this);
		}

		if (result == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (key == null) {
					query.append(_FINDER_COLUMN_KEY_KEY_1);
				}
				else {
					if (key.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_KEY_KEY_3);
					}
					else {
						query.append(_FINDER_COLUMN_KEY_KEY_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (key != null) {
					qPos.add(key);
				}

				List<JIRAIssue> list = q.list();

				result = list;

				JIRAIssue jiraIssue = null;

				if (list.isEmpty()) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_KEY,
						finderArgs, list);
				}
				else {
					jiraIssue = list.get(0);

					cacheResult(jiraIssue);

					if ((jiraIssue.getKey() == null) ||
							!jiraIssue.getKey().equals(key)) {
						FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_KEY,
							finderArgs, jiraIssue);
					}
				}

				return jiraIssue;
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (result == null) {
					FinderCacheUtil.putResult(FINDER_PATH_FETCH_BY_KEY,
						finderArgs, new ArrayList<JIRAIssue>());
				}

				closeSession(session);
			}
		}
		else {
			if (result instanceof List<?>) {
				return null;
			}
			else {
				return (JIRAIssue)result;
			}
		}
	}

	public List<JIRAIssue> findByReporterJiraUserId(String reporterJiraUserId)
		throws SystemException {
		Object[] finderArgs = new Object[] { reporterJiraUserId };

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_REPORTERJIRAUSERID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_REPORTERJIRAUSERID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByReporterJiraUserId(String reporterJiraUserId,
		int start, int end) throws SystemException {
		return findByReporterJiraUserId(reporterJiraUserId, start, end, null);
	}

	public List<JIRAIssue> findByReporterJiraUserId(String reporterJiraUserId,
		int start, int end, OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				reporterJiraUserId,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_REPORTERJIRAUSERID,
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

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_REPORTERJIRAUSERID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByReporterJiraUserId_First(String reporterJiraUserId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByReporterJiraUserId(reporterJiraUserId, 0,
				1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByReporterJiraUserId_Last(String reporterJiraUserId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		int count = countByReporterJiraUserId(reporterJiraUserId);

		List<JIRAIssue> list = findByReporterJiraUserId(reporterJiraUserId,
				count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByReporterJiraUserId_PrevAndNext(long jiraIssueId,
		String reporterJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByReporterJiraUserId(reporterJiraUserId);

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

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			if (reporterJiraUserId == null) {
				query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_1);
			}
			else {
				if (reporterJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			if (reporterJiraUserId != null) {
				qPos.add(reporterJiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByAssigneeJiraUserId(String assigneeJiraUserId)
		throws SystemException {
		Object[] finderArgs = new Object[] { assigneeJiraUserId };

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_ASSIGNEEJIRAUSERID,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_ASSIGNEEJIRAUSERID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByAssigneeJiraUserId(String assigneeJiraUserId,
		int start, int end) throws SystemException {
		return findByAssigneeJiraUserId(assigneeJiraUserId, start, end, null);
	}

	public List<JIRAIssue> findByAssigneeJiraUserId(String assigneeJiraUserId,
		int start, int end, OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				assigneeJiraUserId,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_ASSIGNEEJIRAUSERID,
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

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_ASSIGNEEJIRAUSERID,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByAssigneeJiraUserId_First(String assigneeJiraUserId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByAssigneeJiraUserId(assigneeJiraUserId, 0,
				1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByAssigneeJiraUserId_Last(String assigneeJiraUserId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		int count = countByAssigneeJiraUserId(assigneeJiraUserId);

		List<JIRAIssue> list = findByAssigneeJiraUserId(assigneeJiraUserId,
				count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(4);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByAssigneeJiraUserId_PrevAndNext(long jiraIssueId,
		String assigneeJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByAssigneeJiraUserId(assigneeJiraUserId);

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

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			if (assigneeJiraUserId == null) {
				query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_1);
			}
			else {
				if (assigneeJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			if (assigneeJiraUserId != null) {
				qPos.add(assigneeJiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByMD_P(Date modifiedDate, long projectId)
		throws SystemException {
		Object[] finderArgs = new Object[] { modifiedDate, new Long(projectId) };

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_MD_P,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_PROJECTID_2);

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_MD_P, finderArgs,
					list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByMD_P(Date modifiedDate, long projectId,
		int start, int end) throws SystemException {
		return findByMD_P(modifiedDate, projectId, start, end, null);
	}

	public List<JIRAIssue> findByMD_P(Date modifiedDate, long projectId,
		int start, int end, OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_MD_P,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(4 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(4);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_PROJECTID_2);

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_MD_P,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByMD_P_First(Date modifiedDate, long projectId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByMD_P(modifiedDate, projectId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("modifiedDate=");
			msg.append(modifiedDate);

			msg.append(", projectId=");
			msg.append(projectId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByMD_P_Last(Date modifiedDate, long projectId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		int count = countByMD_P(modifiedDate, projectId);

		List<JIRAIssue> list = findByMD_P(modifiedDate, projectId, count - 1,
				count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("modifiedDate=");
			msg.append(modifiedDate);

			msg.append(", projectId=");
			msg.append(projectId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByMD_P_PrevAndNext(long jiraIssueId,
		Date modifiedDate, long projectId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByMD_P(modifiedDate, projectId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(4 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(4);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			if (modifiedDate == null) {
				query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_1);
			}
			else {
				query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_2);
			}

			query.append(_FINDER_COLUMN_MD_P_PROJECTID_2);

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			if (modifiedDate != null) {
				qPos.add(CalendarUtil.getTimestamp(modifiedDate));
			}

			qPos.add(projectId);

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByP_RJUI(long projectId,
		String reporterJiraUserId) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				reporterJiraUserId
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_P_RJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_RJUI_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_P_RJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByP_RJUI(long projectId,
		String reporterJiraUserId, int start, int end)
		throws SystemException {
		return findByP_RJUI(projectId, reporterJiraUserId, start, end, null);
	}

	public List<JIRAIssue> findByP_RJUI(long projectId,
		String reporterJiraUserId, int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				reporterJiraUserId,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_P_RJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(4 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(4);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_RJUI_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_P_RJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByP_RJUI_First(long projectId,
		String reporterJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByP_RJUI(projectId, reporterJiraUserId, 0,
				1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByP_RJUI_Last(long projectId,
		String reporterJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByP_RJUI(projectId, reporterJiraUserId);

		List<JIRAIssue> list = findByP_RJUI(projectId, reporterJiraUserId,
				count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByP_RJUI_PrevAndNext(long jiraIssueId,
		long projectId, String reporterJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByP_RJUI(projectId, reporterJiraUserId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(4 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(4);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			query.append(_FINDER_COLUMN_P_RJUI_PROJECTID_2);

			if (reporterJiraUserId == null) {
				query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_1);
			}
			else {
				if (reporterJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(projectId);

			if (reporterJiraUserId != null) {
				qPos.add(reporterJiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByP_AJUI(long projectId,
		String assigneeJiraUserId) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				assigneeJiraUserId
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_P_AJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_AJUI_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_P_AJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByP_AJUI(long projectId,
		String assigneeJiraUserId, int start, int end)
		throws SystemException {
		return findByP_AJUI(projectId, assigneeJiraUserId, start, end, null);
	}

	public List<JIRAIssue> findByP_AJUI(long projectId,
		String assigneeJiraUserId, int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				assigneeJiraUserId,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_P_AJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(4 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(4);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_AJUI_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_P_AJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByP_AJUI_First(long projectId,
		String assigneeJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByP_AJUI(projectId, assigneeJiraUserId, 0,
				1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByP_AJUI_Last(long projectId,
		String assigneeJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByP_AJUI(projectId, assigneeJiraUserId);

		List<JIRAIssue> list = findByP_AJUI(projectId, assigneeJiraUserId,
				count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(6);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByP_AJUI_PrevAndNext(long jiraIssueId,
		long projectId, String assigneeJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByP_AJUI(projectId, assigneeJiraUserId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(4 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(4);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			query.append(_FINDER_COLUMN_P_AJUI_PROJECTID_2);

			if (assigneeJiraUserId == null) {
				query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_1);
			}
			else {
				if (assigneeJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(projectId);

			if (assigneeJiraUserId != null) {
				qPos.add(assigneeJiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByMD_P_RJUI(Date modifiedDate, long projectId,
		String reporterJiraUserId) throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				reporterJiraUserId
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_MD_P_RJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(5);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_RJUI_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_MD_P_RJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByMD_P_RJUI(Date modifiedDate, long projectId,
		String reporterJiraUserId, int start, int end)
		throws SystemException {
		return findByMD_P_RJUI(modifiedDate, projectId, reporterJiraUserId,
			start, end, null);
	}

	public List<JIRAIssue> findByMD_P_RJUI(Date modifiedDate, long projectId,
		String reporterJiraUserId, int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				reporterJiraUserId,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_MD_P_RJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(5 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(5);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_RJUI_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_MD_P_RJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByMD_P_RJUI_First(Date modifiedDate, long projectId,
		String reporterJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByMD_P_RJUI(modifiedDate, projectId,
				reporterJiraUserId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("modifiedDate=");
			msg.append(modifiedDate);

			msg.append(", projectId=");
			msg.append(projectId);

			msg.append(", reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByMD_P_RJUI_Last(Date modifiedDate, long projectId,
		String reporterJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByMD_P_RJUI(modifiedDate, projectId, reporterJiraUserId);

		List<JIRAIssue> list = findByMD_P_RJUI(modifiedDate, projectId,
				reporterJiraUserId, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("modifiedDate=");
			msg.append(modifiedDate);

			msg.append(", projectId=");
			msg.append(projectId);

			msg.append(", reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByMD_P_RJUI_PrevAndNext(long jiraIssueId,
		Date modifiedDate, long projectId, String reporterJiraUserId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByMD_P_RJUI(modifiedDate, projectId, reporterJiraUserId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(5 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(5);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			if (modifiedDate == null) {
				query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_1);
			}
			else {
				query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_2);
			}

			query.append(_FINDER_COLUMN_MD_P_RJUI_PROJECTID_2);

			if (reporterJiraUserId == null) {
				query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_1);
			}
			else {
				if (reporterJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			if (modifiedDate != null) {
				qPos.add(CalendarUtil.getTimestamp(modifiedDate));
			}

			qPos.add(projectId);

			if (reporterJiraUserId != null) {
				qPos.add(reporterJiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByMD_P_AJUI(Date modifiedDate, long projectId,
		String assigneeJiraUserId) throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				assigneeJiraUserId
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_MD_P_AJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(5);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_AJUI_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_MD_P_AJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByMD_P_AJUI(Date modifiedDate, long projectId,
		String assigneeJiraUserId, int start, int end)
		throws SystemException {
		return findByMD_P_AJUI(modifiedDate, projectId, assigneeJiraUserId,
			start, end, null);
	}

	public List<JIRAIssue> findByMD_P_AJUI(Date modifiedDate, long projectId,
		String assigneeJiraUserId, int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				assigneeJiraUserId,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_MD_P_AJUI,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(5 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(5);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_AJUI_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_MD_P_AJUI,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByMD_P_AJUI_First(Date modifiedDate, long projectId,
		String assigneeJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByMD_P_AJUI(modifiedDate, projectId,
				assigneeJiraUserId, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("modifiedDate=");
			msg.append(modifiedDate);

			msg.append(", projectId=");
			msg.append(projectId);

			msg.append(", assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByMD_P_AJUI_Last(Date modifiedDate, long projectId,
		String assigneeJiraUserId, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByMD_P_AJUI(modifiedDate, projectId, assigneeJiraUserId);

		List<JIRAIssue> list = findByMD_P_AJUI(modifiedDate, projectId,
				assigneeJiraUserId, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("modifiedDate=");
			msg.append(modifiedDate);

			msg.append(", projectId=");
			msg.append(projectId);

			msg.append(", assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByMD_P_AJUI_PrevAndNext(long jiraIssueId,
		Date modifiedDate, long projectId, String assigneeJiraUserId,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByMD_P_AJUI(modifiedDate, projectId, assigneeJiraUserId);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(5 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(5);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			if (modifiedDate == null) {
				query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_1);
			}
			else {
				query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_2);
			}

			query.append(_FINDER_COLUMN_MD_P_AJUI_PROJECTID_2);

			if (assigneeJiraUserId == null) {
				query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_1);
			}
			else {
				if (assigneeJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			if (modifiedDate != null) {
				qPos.add(CalendarUtil.getTimestamp(modifiedDate));
			}

			qPos.add(projectId);

			if (assigneeJiraUserId != null) {
				qPos.add(assigneeJiraUserId);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByP_RJUI_S(long projectId,
		String reporterJiraUserId, String status) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				reporterJiraUserId,
				
				status
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_P_RJUI_S,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(5);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_RJUI_S_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_2);
					}
				}

				if (status == null) {
					query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_1);
				}
				else {
					if (status.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				if (status != null) {
					qPos.add(status);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_P_RJUI_S,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByP_RJUI_S(long projectId,
		String reporterJiraUserId, String status, int start, int end)
		throws SystemException {
		return findByP_RJUI_S(projectId, reporterJiraUserId, status, start,
			end, null);
	}

	public List<JIRAIssue> findByP_RJUI_S(long projectId,
		String reporterJiraUserId, String status, int start, int end,
		OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				reporterJiraUserId,
				
				status,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_P_RJUI_S,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(5 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(5);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_RJUI_S_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_2);
					}
				}

				if (status == null) {
					query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_1);
				}
				else {
					if (status.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				if (status != null) {
					qPos.add(status);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_P_RJUI_S,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByP_RJUI_S_First(long projectId,
		String reporterJiraUserId, String status, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByP_RJUI_S(projectId, reporterJiraUserId,
				status, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(", status=");
			msg.append(status);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByP_RJUI_S_Last(long projectId,
		String reporterJiraUserId, String status, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByP_RJUI_S(projectId, reporterJiraUserId, status);

		List<JIRAIssue> list = findByP_RJUI_S(projectId, reporterJiraUserId,
				status, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", reporterJiraUserId=");
			msg.append(reporterJiraUserId);

			msg.append(", status=");
			msg.append(status);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByP_RJUI_S_PrevAndNext(long jiraIssueId,
		long projectId, String reporterJiraUserId, String status,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByP_RJUI_S(projectId, reporterJiraUserId, status);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(5 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(5);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			query.append(_FINDER_COLUMN_P_RJUI_S_PROJECTID_2);

			if (reporterJiraUserId == null) {
				query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_1);
			}
			else {
				if (reporterJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_2);
				}
			}

			if (status == null) {
				query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_1);
			}
			else {
				if (status.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_3);
				}
				else {
					query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(projectId);

			if (reporterJiraUserId != null) {
				qPos.add(reporterJiraUserId);
			}

			if (status != null) {
				qPos.add(status);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

			return array;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<JIRAIssue> findByP_AJUI_S(long projectId,
		String assigneeJiraUserId, String status) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				assigneeJiraUserId,
				
				status
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_P_AJUI_S,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(5);

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_AJUI_S_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_2);
					}
				}

				if (status == null) {
					query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_1);
				}
				else {
					if (status.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_2);
					}
				}

				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				if (status != null) {
					qPos.add(status);
				}

				list = q.list();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_P_AJUI_S,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public List<JIRAIssue> findByP_AJUI_S(long projectId,
		String assigneeJiraUserId, String status, int start, int end)
		throws SystemException {
		return findByP_AJUI_S(projectId, assigneeJiraUserId, status, start,
			end, null);
	}

	public List<JIRAIssue> findByP_AJUI_S(long projectId,
		String assigneeJiraUserId, String status, int start, int end,
		OrderByComparator obc) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				assigneeJiraUserId,
				
				status,
				
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_BY_OBC_P_AJUI_S,
				finderArgs, this);

		if (list == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = null;

				if (obc != null) {
					query = new StringBundler(5 +
							(obc.getOrderByFields().length * 3));
				}
				else {
					query = new StringBundler(5);
				}

				query.append(_SQL_SELECT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_AJUI_S_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_2);
					}
				}

				if (status == null) {
					query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_1);
				}
				else {
					if (status.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_2);
					}
				}

				if (obc != null) {
					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
				}

				else {
					query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				if (status != null) {
					qPos.add(status);
				}

				list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(), start,
						end);
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_BY_OBC_P_AJUI_S,
					finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public JIRAIssue findByP_AJUI_S_First(long projectId,
		String assigneeJiraUserId, String status, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		List<JIRAIssue> list = findByP_AJUI_S(projectId, assigneeJiraUserId,
				status, 0, 1, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(", status=");
			msg.append(status);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue findByP_AJUI_S_Last(long projectId,
		String assigneeJiraUserId, String status, OrderByComparator obc)
		throws NoSuchJIRAIssueException, SystemException {
		int count = countByP_AJUI_S(projectId, assigneeJiraUserId, status);

		List<JIRAIssue> list = findByP_AJUI_S(projectId, assigneeJiraUserId,
				status, count - 1, count, obc);

		if (list.isEmpty()) {
			StringBundler msg = new StringBundler(8);

			msg.append(_NO_SUCH_ENTITY_WITH_KEY);

			msg.append("projectId=");
			msg.append(projectId);

			msg.append(", assigneeJiraUserId=");
			msg.append(assigneeJiraUserId);

			msg.append(", status=");
			msg.append(status);

			msg.append(StringPool.CLOSE_CURLY_BRACE);

			throw new NoSuchJIRAIssueException(msg.toString());
		}
		else {
			return list.get(0);
		}
	}

	public JIRAIssue[] findByP_AJUI_S_PrevAndNext(long jiraIssueId,
		long projectId, String assigneeJiraUserId, String status,
		OrderByComparator obc) throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByPrimaryKey(jiraIssueId);

		int count = countByP_AJUI_S(projectId, assigneeJiraUserId, status);

		Session session = null;

		try {
			session = openSession();

			StringBundler query = null;

			if (obc != null) {
				query = new StringBundler(5 +
						(obc.getOrderByFields().length * 3));
			}
			else {
				query = new StringBundler(5);
			}

			query.append(_SQL_SELECT_JIRAISSUE_WHERE);

			query.append(_FINDER_COLUMN_P_AJUI_S_PROJECTID_2);

			if (assigneeJiraUserId == null) {
				query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_1);
			}
			else {
				if (assigneeJiraUserId.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_3);
				}
				else {
					query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_2);
				}
			}

			if (status == null) {
				query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_1);
			}
			else {
				if (status.equals(StringPool.BLANK)) {
					query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_3);
				}
				else {
					query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_2);
				}
			}

			if (obc != null) {
				appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);
			}

			else {
				query.append(JIRAIssueModelImpl.ORDER_BY_JPQL);
			}

			String sql = query.toString();

			Query q = session.createQuery(sql);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(projectId);

			if (assigneeJiraUserId != null) {
				qPos.add(assigneeJiraUserId);
			}

			if (status != null) {
				qPos.add(status);
			}

			Object[] objArray = QueryUtil.getPrevAndNext(q, count, obc,
					jiraIssue);

			JIRAIssue[] array = new JIRAIssueImpl[3];

			array[0] = (JIRAIssue)objArray[0];
			array[1] = (JIRAIssue)objArray[1];
			array[2] = (JIRAIssue)objArray[2];

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

	public List<JIRAIssue> findAll() throws SystemException {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
	}

	public List<JIRAIssue> findAll(int start, int end)
		throws SystemException {
		return findAll(start, end, null);
	}

	public List<JIRAIssue> findAll(int start, int end, OrderByComparator obc)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				String.valueOf(start), String.valueOf(end), String.valueOf(obc)
			};

		List<JIRAIssue> list = (List<JIRAIssue>)FinderCacheUtil.getResult(FINDER_PATH_FIND_ALL,
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

					query.append(_SQL_SELECT_JIRAISSUE);

					appendOrderByComparator(query, _ORDER_BY_ENTITY_ALIAS, obc);

					sql = query.toString();
				}

				else {
					sql = _SQL_SELECT_JIRAISSUE.concat(JIRAIssueModelImpl.ORDER_BY_JPQL);
				}

				Query q = session.createQuery(sql);

				if (obc == null) {
					list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(),
							start, end, false);

					Collections.sort(list);
				}
				else {
					list = (List<JIRAIssue>)QueryUtil.list(q, getDialect(),
							start, end);
				}
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (list == null) {
					list = new ArrayList<JIRAIssue>();
				}

				cacheResult(list);

				FinderCacheUtil.putResult(FINDER_PATH_FIND_ALL, finderArgs, list);

				closeSession(session);
			}
		}

		return list;
	}

	public void removeByProjectId(long projectId) throws SystemException {
		for (JIRAIssue jiraIssue : findByProjectId(projectId)) {
			remove(jiraIssue);
		}
	}

	public void removeByKey(String key)
		throws NoSuchJIRAIssueException, SystemException {
		JIRAIssue jiraIssue = findByKey(key);

		remove(jiraIssue);
	}

	public void removeByReporterJiraUserId(String reporterJiraUserId)
		throws SystemException {
		for (JIRAIssue jiraIssue : findByReporterJiraUserId(reporterJiraUserId)) {
			remove(jiraIssue);
		}
	}

	public void removeByAssigneeJiraUserId(String assigneeJiraUserId)
		throws SystemException {
		for (JIRAIssue jiraIssue : findByAssigneeJiraUserId(assigneeJiraUserId)) {
			remove(jiraIssue);
		}
	}

	public void removeByMD_P(Date modifiedDate, long projectId)
		throws SystemException {
		for (JIRAIssue jiraIssue : findByMD_P(modifiedDate, projectId)) {
			remove(jiraIssue);
		}
	}

	public void removeByP_RJUI(long projectId, String reporterJiraUserId)
		throws SystemException {
		for (JIRAIssue jiraIssue : findByP_RJUI(projectId, reporterJiraUserId)) {
			remove(jiraIssue);
		}
	}

	public void removeByP_AJUI(long projectId, String assigneeJiraUserId)
		throws SystemException {
		for (JIRAIssue jiraIssue : findByP_AJUI(projectId, assigneeJiraUserId)) {
			remove(jiraIssue);
		}
	}

	public void removeByMD_P_RJUI(Date modifiedDate, long projectId,
		String reporterJiraUserId) throws SystemException {
		for (JIRAIssue jiraIssue : findByMD_P_RJUI(modifiedDate, projectId,
				reporterJiraUserId)) {
			remove(jiraIssue);
		}
	}

	public void removeByMD_P_AJUI(Date modifiedDate, long projectId,
		String assigneeJiraUserId) throws SystemException {
		for (JIRAIssue jiraIssue : findByMD_P_AJUI(modifiedDate, projectId,
				assigneeJiraUserId)) {
			remove(jiraIssue);
		}
	}

	public void removeByP_RJUI_S(long projectId, String reporterJiraUserId,
		String status) throws SystemException {
		for (JIRAIssue jiraIssue : findByP_RJUI_S(projectId,
				reporterJiraUserId, status)) {
			remove(jiraIssue);
		}
	}

	public void removeByP_AJUI_S(long projectId, String assigneeJiraUserId,
		String status) throws SystemException {
		for (JIRAIssue jiraIssue : findByP_AJUI_S(projectId,
				assigneeJiraUserId, status)) {
			remove(jiraIssue);
		}
	}

	public void removeAll() throws SystemException {
		for (JIRAIssue jiraIssue : findAll()) {
			remove(jiraIssue);
		}
	}

	public int countByProjectId(long projectId) throws SystemException {
		Object[] finderArgs = new Object[] { new Long(projectId) };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_PROJECTID,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(2);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_PROJECTID_PROJECTID_2);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_PROJECTID,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByKey(String key) throws SystemException {
		Object[] finderArgs = new Object[] { key };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_KEY,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(2);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				if (key == null) {
					query.append(_FINDER_COLUMN_KEY_KEY_1);
				}
				else {
					if (key.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_KEY_KEY_3);
					}
					else {
						query.append(_FINDER_COLUMN_KEY_KEY_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (key != null) {
					qPos.add(key);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_KEY, finderArgs,
					count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByReporterJiraUserId(String reporterJiraUserId)
		throws SystemException {
		Object[] finderArgs = new Object[] { reporterJiraUserId };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_REPORTERJIRAUSERID,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(2);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_REPORTERJIRAUSERID,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByAssigneeJiraUserId(String assigneeJiraUserId)
		throws SystemException {
		Object[] finderArgs = new Object[] { assigneeJiraUserId };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_ASSIGNEEJIRAUSERID,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(2);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_ASSIGNEEJIRAUSERID,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByMD_P(Date modifiedDate, long projectId)
		throws SystemException {
		Object[] finderArgs = new Object[] { modifiedDate, new Long(projectId) };

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_MD_P,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_PROJECTID_2);

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_MD_P,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByP_RJUI(long projectId, String reporterJiraUserId)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				reporterJiraUserId
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_P_RJUI,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_RJUI_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_P_RJUI,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByP_AJUI(long projectId, String assigneeJiraUserId)
		throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				assigneeJiraUserId
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_P_AJUI,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(3);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_AJUI_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_P_AJUI,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByMD_P_RJUI(Date modifiedDate, long projectId,
		String reporterJiraUserId) throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				reporterJiraUserId
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_MD_P_RJUI,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_RJUI_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_MD_P_RJUI,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByMD_P_AJUI(Date modifiedDate, long projectId,
		String assigneeJiraUserId) throws SystemException {
		Object[] finderArgs = new Object[] {
				modifiedDate, new Long(projectId),
				
				assigneeJiraUserId
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_MD_P_AJUI,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				if (modifiedDate == null) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_1);
				}
				else {
					query.append(_FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_2);
				}

				query.append(_FINDER_COLUMN_MD_P_AJUI_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				if (modifiedDate != null) {
					qPos.add(CalendarUtil.getTimestamp(modifiedDate));
				}

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_MD_P_AJUI,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByP_RJUI_S(long projectId, String reporterJiraUserId,
		String status) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				reporterJiraUserId,
				
				status
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_P_RJUI_S,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_RJUI_S_PROJECTID_2);

				if (reporterJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_1);
				}
				else {
					if (reporterJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_2);
					}
				}

				if (status == null) {
					query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_1);
				}
				else {
					if (status.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_RJUI_S_STATUS_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (reporterJiraUserId != null) {
					qPos.add(reporterJiraUserId);
				}

				if (status != null) {
					qPos.add(status);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_P_RJUI_S,
					finderArgs, count);

				closeSession(session);
			}
		}

		return count.intValue();
	}

	public int countByP_AJUI_S(long projectId, String assigneeJiraUserId,
		String status) throws SystemException {
		Object[] finderArgs = new Object[] {
				new Long(projectId),
				
				assigneeJiraUserId,
				
				status
			};

		Long count = (Long)FinderCacheUtil.getResult(FINDER_PATH_COUNT_BY_P_AJUI_S,
				finderArgs, this);

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				StringBundler query = new StringBundler(4);

				query.append(_SQL_COUNT_JIRAISSUE_WHERE);

				query.append(_FINDER_COLUMN_P_AJUI_S_PROJECTID_2);

				if (assigneeJiraUserId == null) {
					query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_1);
				}
				else {
					if (assigneeJiraUserId.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_2);
					}
				}

				if (status == null) {
					query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_1);
				}
				else {
					if (status.equals(StringPool.BLANK)) {
						query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_3);
					}
					else {
						query.append(_FINDER_COLUMN_P_AJUI_S_STATUS_2);
					}
				}

				String sql = query.toString();

				Query q = session.createQuery(sql);

				QueryPos qPos = QueryPos.getInstance(q);

				qPos.add(projectId);

				if (assigneeJiraUserId != null) {
					qPos.add(assigneeJiraUserId);
				}

				if (status != null) {
					qPos.add(status);
				}

				count = (Long)q.uniqueResult();
			}
			catch (Exception e) {
				throw processException(e);
			}
			finally {
				if (count == null) {
					count = Long.valueOf(0);
				}

				FinderCacheUtil.putResult(FINDER_PATH_COUNT_BY_P_AJUI_S,
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

				Query q = session.createQuery(_SQL_COUNT_JIRAISSUE);

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
						"value.object.listener.com.liferay.socialcoding.model.JIRAIssue")));

		if (listenerClassNames.length > 0) {
			try {
				List<ModelListener<JIRAIssue>> listenersList = new ArrayList<ModelListener<JIRAIssue>>();

				for (String listenerClassName : listenerClassNames) {
					listenersList.add((ModelListener<JIRAIssue>)Class.forName(
							listenerClassName).newInstance());
				}

				listeners = listenersList.toArray(new ModelListener[listenersList.size()]);
			}
			catch (Exception e) {
				_log.error(e);
			}
		}
	}

	@BeanReference(name = "com.liferay.socialcoding.service.persistence.JIRAActionPersistence")
	protected com.liferay.socialcoding.service.persistence.JIRAActionPersistence jiraActionPersistence;
	@BeanReference(name = "com.liferay.socialcoding.service.persistence.JIRAChangeGroupPersistence")
	protected com.liferay.socialcoding.service.persistence.JIRAChangeGroupPersistence jiraChangeGroupPersistence;
	@BeanReference(name = "com.liferay.socialcoding.service.persistence.JIRAChangeItemPersistence")
	protected com.liferay.socialcoding.service.persistence.JIRAChangeItemPersistence jiraChangeItemPersistence;
	@BeanReference(name = "com.liferay.socialcoding.service.persistence.JIRAIssuePersistence")
	protected com.liferay.socialcoding.service.persistence.JIRAIssuePersistence jiraIssuePersistence;
	@BeanReference(name = "com.liferay.socialcoding.service.persistence.SVNRepositoryPersistence")
	protected com.liferay.socialcoding.service.persistence.SVNRepositoryPersistence svnRepositoryPersistence;
	@BeanReference(name = "com.liferay.socialcoding.service.persistence.SVNRevisionPersistence")
	protected com.liferay.socialcoding.service.persistence.SVNRevisionPersistence svnRevisionPersistence;
	@BeanReference(name = "com.liferay.portal.service.persistence.ResourcePersistence")
	protected com.liferay.portal.service.persistence.ResourcePersistence resourcePersistence;
	@BeanReference(name = "com.liferay.portal.service.persistence.UserPersistence")
	protected com.liferay.portal.service.persistence.UserPersistence userPersistence;
	private static final String _SQL_SELECT_JIRAISSUE = "SELECT jiraIssue FROM JIRAIssue jiraIssue";
	private static final String _SQL_SELECT_JIRAISSUE_WHERE = "SELECT jiraIssue FROM JIRAIssue jiraIssue WHERE ";
	private static final String _SQL_COUNT_JIRAISSUE = "SELECT COUNT(jiraIssue) FROM JIRAIssue jiraIssue";
	private static final String _SQL_COUNT_JIRAISSUE_WHERE = "SELECT COUNT(jiraIssue) FROM JIRAIssue jiraIssue WHERE ";
	private static final String _FINDER_COLUMN_PROJECTID_PROJECTID_2 = "jiraIssue.projectId = ?";
	private static final String _FINDER_COLUMN_KEY_KEY_1 = "jiraIssue.key IS NULL";
	private static final String _FINDER_COLUMN_KEY_KEY_2 = "jiraIssue.key = ?";
	private static final String _FINDER_COLUMN_KEY_KEY_3 = "(jiraIssue.key IS NULL OR jiraIssue.key = ?)";
	private static final String _FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_1 =
		"jiraIssue.reporterJiraUserId IS NULL";
	private static final String _FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_2 =
		"jiraIssue.reporterJiraUserId = ?";
	private static final String _FINDER_COLUMN_REPORTERJIRAUSERID_REPORTERJIRAUSERID_3 =
		"(jiraIssue.reporterJiraUserId IS NULL OR jiraIssue.reporterJiraUserId = ?)";
	private static final String _FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_1 =
		"jiraIssue.assigneeJiraUserId IS NULL";
	private static final String _FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_2 =
		"jiraIssue.assigneeJiraUserId = ?";
	private static final String _FINDER_COLUMN_ASSIGNEEJIRAUSERID_ASSIGNEEJIRAUSERID_3 =
		"(jiraIssue.assigneeJiraUserId IS NULL OR jiraIssue.assigneeJiraUserId = ?)";
	private static final String _FINDER_COLUMN_MD_P_MODIFIEDDATE_1 = "jiraIssue.modifiedDate > NULL AND ";
	private static final String _FINDER_COLUMN_MD_P_MODIFIEDDATE_2 = "jiraIssue.modifiedDate > ? AND ";
	private static final String _FINDER_COLUMN_MD_P_PROJECTID_2 = "jiraIssue.projectId = ?";
	private static final String _FINDER_COLUMN_P_RJUI_PROJECTID_2 = "jiraIssue.projectId = ? AND ";
	private static final String _FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_1 = "jiraIssue.reporterJiraUserId IS NULL";
	private static final String _FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_2 = "jiraIssue.reporterJiraUserId = ?";
	private static final String _FINDER_COLUMN_P_RJUI_REPORTERJIRAUSERID_3 = "(jiraIssue.reporterJiraUserId IS NULL OR jiraIssue.reporterJiraUserId = ?)";
	private static final String _FINDER_COLUMN_P_AJUI_PROJECTID_2 = "jiraIssue.projectId = ? AND ";
	private static final String _FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_1 = "jiraIssue.assigneeJiraUserId IS NULL";
	private static final String _FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_2 = "jiraIssue.assigneeJiraUserId = ?";
	private static final String _FINDER_COLUMN_P_AJUI_ASSIGNEEJIRAUSERID_3 = "(jiraIssue.assigneeJiraUserId IS NULL OR jiraIssue.assigneeJiraUserId = ?)";
	private static final String _FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_1 = "jiraIssue.modifiedDate > NULL AND ";
	private static final String _FINDER_COLUMN_MD_P_RJUI_MODIFIEDDATE_2 = "jiraIssue.modifiedDate > ? AND ";
	private static final String _FINDER_COLUMN_MD_P_RJUI_PROJECTID_2 = "jiraIssue.projectId = ? AND ";
	private static final String _FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_1 = "jiraIssue.reporterJiraUserId IS NULL";
	private static final String _FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_2 = "jiraIssue.reporterJiraUserId = ?";
	private static final String _FINDER_COLUMN_MD_P_RJUI_REPORTERJIRAUSERID_3 = "(jiraIssue.reporterJiraUserId IS NULL OR jiraIssue.reporterJiraUserId = ?)";
	private static final String _FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_1 = "jiraIssue.modifiedDate > NULL AND ";
	private static final String _FINDER_COLUMN_MD_P_AJUI_MODIFIEDDATE_2 = "jiraIssue.modifiedDate > ? AND ";
	private static final String _FINDER_COLUMN_MD_P_AJUI_PROJECTID_2 = "jiraIssue.projectId = ? AND ";
	private static final String _FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_1 = "jiraIssue.assigneeJiraUserId IS NULL";
	private static final String _FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_2 = "jiraIssue.assigneeJiraUserId = ?";
	private static final String _FINDER_COLUMN_MD_P_AJUI_ASSIGNEEJIRAUSERID_3 = "(jiraIssue.assigneeJiraUserId IS NULL OR jiraIssue.assigneeJiraUserId = ?)";
	private static final String _FINDER_COLUMN_P_RJUI_S_PROJECTID_2 = "jiraIssue.projectId = ? AND ";
	private static final String _FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_1 = "jiraIssue.reporterJiraUserId IS NULL AND ";
	private static final String _FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_2 = "jiraIssue.reporterJiraUserId = ? AND ";
	private static final String _FINDER_COLUMN_P_RJUI_S_REPORTERJIRAUSERID_3 = "(jiraIssue.reporterJiraUserId IS NULL OR jiraIssue.reporterJiraUserId = ?) AND ";
	private static final String _FINDER_COLUMN_P_RJUI_S_STATUS_1 = "jiraIssue.status IS NULL";
	private static final String _FINDER_COLUMN_P_RJUI_S_STATUS_2 = "jiraIssue.status = ?";
	private static final String _FINDER_COLUMN_P_RJUI_S_STATUS_3 = "(jiraIssue.status IS NULL OR jiraIssue.status = ?)";
	private static final String _FINDER_COLUMN_P_AJUI_S_PROJECTID_2 = "jiraIssue.projectId = ? AND ";
	private static final String _FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_1 = "jiraIssue.assigneeJiraUserId IS NULL AND ";
	private static final String _FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_2 = "jiraIssue.assigneeJiraUserId = ? AND ";
	private static final String _FINDER_COLUMN_P_AJUI_S_ASSIGNEEJIRAUSERID_3 = "(jiraIssue.assigneeJiraUserId IS NULL OR jiraIssue.assigneeJiraUserId = ?) AND ";
	private static final String _FINDER_COLUMN_P_AJUI_S_STATUS_1 = "jiraIssue.status IS NULL";
	private static final String _FINDER_COLUMN_P_AJUI_S_STATUS_2 = "jiraIssue.status = ?";
	private static final String _FINDER_COLUMN_P_AJUI_S_STATUS_3 = "(jiraIssue.status IS NULL OR jiraIssue.status = ?)";
	private static final String _ORDER_BY_ENTITY_ALIAS = "jiraIssue.";
	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY = "No JIRAIssue exists with the primary key ";
	private static final String _NO_SUCH_ENTITY_WITH_KEY = "No JIRAIssue exists with the key {";
	private static Log _log = LogFactoryUtil.getLog(JIRAIssuePersistenceImpl.class);
}