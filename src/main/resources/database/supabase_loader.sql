---------------- extension.sql --------------------
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

---------------- identity.sql --------------------

CREATE SCHEMA IF NOT EXISTS identity;

CREATE TYPE identity.user_role AS ENUM (
    'VIEWER',
    'LIBRARY_ADMIN'
);

CREATE TABLE identity.user_profiles (
    id UUID PRIMARY KEY
        REFERENCES auth.users(id)
        ON DELETE CASCADE,

    role identity.user_role
        NOT NULL DEFAULT 'VIEWER',

    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

---------------- library.sql --------------------

CREATE SCHEMA IF NOT EXISTS library;

CREATE TYPE library.content_type AS ENUM (
    'MOVIE',
    'SERIES'
);

CREATE TYPE library.genre AS ENUM (
    'ACTION',
    'COMEDY',
    'DRAMA',
    'DOCUMENTARY',
    'HORROR'
);

CREATE TABLE library.contents (
      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
      content_type library.content_type NOT NULL,
      title VARCHAR(500) NOT NULL,
      description TEXT,
      thumbnail_url VARCHAR(2048),
      genre library.genre NOT NULL,
      release_year INT NOT NULL CHECK (release_year >= 1888),
      available BOOLEAN NOT NULL DEFAULT true,
      created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
      updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE library.movies (
    content_id UUID PRIMARY KEY
        REFERENCES library.contents(id) ON DELETE CASCADE,
    duration_sec INT NOT NULL CHECK (duration_sec > 0),
    video_uri VARCHAR(2048) NOT NULL,
    video_languages TEXT[] NOT NULL DEFAULT '{}'
);

CREATE TABLE library.series (
    content_id UUID PRIMARY KEY
        REFERENCES library.contents(id) ON DELETE CASCADE
);

CREATE TABLE library.seasons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    series_id UUID NOT NULL
        REFERENCES library.series(content_id) ON DELETE CASCADE,
    season_number INT NOT NULL CHECK (season_number > 0),
    title VARCHAR(500) NOT NULL,
    UNIQUE(series_id, season_number)
);

CREATE TABLE library.episodes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    season_id UUID NOT NULL
      REFERENCES library.seasons(id) ON DELETE CASCADE,
    episode_number INT NOT NULL CHECK (episode_number > 0),
    title VARCHAR(500) NOT NULL,
    duration_sec INT NOT NULL CHECK (duration_sec > 0),
    video_uri VARCHAR(2048) NOT NULL,
    video_languages TEXT[] NOT NULL DEFAULT '{}',
    UNIQUE(season_id, episode_number)
);

CREATE TABLE library.content_keywords (
    content_id UUID NOT NULL
      REFERENCES library.contents(id) ON DELETE CASCADE,
    keyword VARCHAR(100) NOT NULL,
    PRIMARY KEY(content_id, keyword)
);

CREATE INDEX idx_library_contents_browse
    ON library.contents(genre, release_year)
    WHERE available = true;

CREATE INDEX idx_library_contents_title_trgm
    ON library.contents
    USING gin(title gin_trgm_ops);

CREATE INDEX idx_library_keywords
    ON library.content_keywords(keyword);

---------------- playback.sql --------------------

CREATE SCHEMA IF NOT EXISTS playback;

CREATE TYPE playback.playback_status AS ENUM (
    'PLAYING',
    'PAUSED',
    'COMPLETED'
);

CREATE TABLE playback.playbacks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    viewer_id UUID NOT NULL
        REFERENCES auth.users(id)
        ON DELETE CASCADE,
    playable_id UUID NOT NULL,
    playable_type VARCHAR(10)
        NOT NULL
        CHECK (playable_type IN ('MOVIE', 'EPISODE')),
    position_seconds INT NOT NULL DEFAULT 0
        CHECK (position_seconds >= 0),
    progress_updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    status playback.playback_status
        NOT NULL DEFAULT 'PLAYING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(viewer_id, playable_type, playable_id)
);

CREATE INDEX idx_playback_viewer
    ON playback.playbacks(viewer_id);

CREATE INDEX idx_playback_status
    ON playback.playbacks(status);

CREATE VIEW playback.watch_activity AS
SELECT
    viewer_id,
    playable_id,
    position_seconds,
    progress_updated_at AS updated_at,
    (status = 'COMPLETED') AS completed
FROM playback.playbacks;

---------------- recommendation.sql --------------------

CREATE SCHEMA IF NOT EXISTS recommendation;

CREATE TABLE recommendation.watchlists (
   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
   viewer_id UUID NOT NULL UNIQUE
       REFERENCES auth.users(id)
       ON DELETE CASCADE,
   created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
   updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE recommendation.watchlist_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    watchlist_id UUID NOT NULL
        REFERENCES recommendation.watchlists(id)
            ON DELETE CASCADE,

    content_id UUID NOT NULL
        REFERENCES library.contents(id)
            ON DELETE CASCADE,

    added_on TIMESTAMPTZ NOT NULL DEFAULT now(),

    UNIQUE (watchlist_id, content_id)
);

CREATE TABLE recommendation.ratings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    viewer_id UUID NOT NULL
        REFERENCES auth.users(id)
        ON DELETE CASCADE,
    content_id UUID NOT NULL
        REFERENCES library.contents(id)
        ON DELETE CASCADE,
    stars SMALLINT NOT NULL
        CHECK (stars BETWEEN 1 AND 5),
    rated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(viewer_id, content_id)
);

CREATE INDEX idx_recommendation_ratings_viewer
    ON recommendation.ratings(viewer_id);

CREATE INDEX idx_recommendation_ratings_content
    ON recommendation.ratings(content_id);

CREATE INDEX idx_recommendation_watchlist_content
    ON recommendation.watchlist_items(content_id);

---------------- rls.sql (do autoryzacji) --------------------

ALTER TABLE identity.user_profiles
    ENABLE ROW LEVEL SECURITY;

--------------- Autoryzacja -------------------

CREATE OR REPLACE FUNCTION identity.handle_new_user()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
INSERT INTO identity.user_profiles(id)
VALUES (NEW.id);

RETURN NEW;
END;
$$;

CREATE TRIGGER on_auth_user_created
    AFTER INSERT ON auth.users
    FOR EACH ROW
    EXECUTE FUNCTION identity.handle_new_user();

CREATE POLICY user_profiles_select_own
ON identity.user_profiles
FOR SELECT
USING (id = auth.uid());

CREATE POLICY user_profiles_update_own
ON identity.user_profiles
FOR UPDATE
USING (id = auth.uid());

-- Watchlist --

DROP POLICY IF EXISTS authenticated_watchlists
ON recommendation.watchlists;

ALTER TABLE recommendation.watchlists ENABLE ROW LEVEL SECURITY;

CREATE POLICY watchlists_select_own
ON recommendation.watchlists
FOR SELECT
USING (viewer_id = auth.uid());

CREATE POLICY watchlists_insert_own
ON recommendation.watchlists
FOR INSERT
WITH CHECK (viewer_id = auth.uid());

CREATE POLICY watchlists_update_own
ON recommendation.watchlists
FOR UPDATE
USING (viewer_id = auth.uid());

CREATE POLICY watchlists_delete_own
ON recommendation.watchlists
FOR DELETE
USING (viewer_id = auth.uid());

-- Watchlist items --

DROP POLICY IF EXISTS authenticated_watchlist_items
ON recommendation.watchlist_items;

ALTER TABLE recommendation.watchlist_items ENABLE ROW LEVEL SECURITY;

CREATE POLICY watchlist_items_all
ON recommendation.watchlist_items
FOR ALL
USING (
    EXISTS (
        SELECT 1
        FROM recommendation.watchlists w
        WHERE w.id = watchlist_id
          AND w.viewer_id = auth.uid()
    )
)
WITH CHECK (
    EXISTS (
        SELECT 1
        FROM recommendation.watchlists w
        WHERE w.id = watchlist_id
          AND w.viewer_id = auth.uid()
    )
);

-- Ratings --

DROP POLICY IF EXISTS authenticated_ratings
ON recommendation.ratings;

ALTER TABLE recommendation.ratings ENABLE ROW LEVEL SECURITY;

CREATE POLICY ratings_all
ON recommendation.ratings
FOR ALL
USING (viewer_id = auth.uid())
WITH CHECK (viewer_id = auth.uid());

-- Playbacks --

DROP POLICY IF EXISTS authenticated_playbacks
ON playback.playbacks;

ALTER TABLE playback.playbacks ENABLE ROW LEVEL SECURITY;

CREATE POLICY playbacks_all
ON playback.playbacks
FOR ALL
USING (viewer_id = auth.uid())
WITH CHECK (viewer_id = auth.uid());

-- Library (biblioteka treści) --

ALTER TABLE library.contents
ENABLE ROW LEVEL SECURITY;

CREATE POLICY contents_public_read
ON library.contents
FOR SELECT
TO authenticated
USING (true);
--
ALTER TABLE library.series
ENABLE ROW LEVEL SECURITY;

CREATE POLICY series_public_read
ON library.series
FOR SELECT
TO authenticated
USING (true);
--
ALTER TABLE library.movies
ENABLE ROW LEVEL SECURITY;

CREATE POLICY movies_public_read
ON library.movies
FOR SELECT
TO authenticated
USING (true);
--
ALTER TABLE library.seasons
ENABLE ROW LEVEL SECURITY;

CREATE POLICY seasons_public_read
ON library.seasons
FOR SELECT
TO authenticated
USING (true);
--
ALTER TABLE library.episodes
ENABLE ROW LEVEL SECURITY;

CREATE POLICY episodes_public_read
ON library.episodes
FOR SELECT
TO authenticated
USING (true);
--
ALTER TABLE library.content_keywords
ENABLE ROW LEVEL SECURITY;

CREATE POLICY content_keywords_public_read
ON library.content_keywords
FOR SELECT
USING (true);

-- Admin biblioteki --

CREATE POLICY library_admin_insert
ON library.contents
FOR INSERT
WITH CHECK (
    EXISTS (
        SELECT 1
        FROM identity.user_profiles p
        WHERE p.id = auth.uid()
          AND p.role = 'LIBRARY_ADMIN'
    )
);

CREATE POLICY library_admin_update
ON library.contents
FOR UPDATE
    USING (
      EXISTS (
        SELECT 1
        FROM identity.user_profiles p
        WHERE p.id = auth.uid()
            AND p.role = 'LIBRARY_ADMIN'
    )
)
WITH CHECK (
    EXISTS (
        SELECT 1
        FROM identity.user_profiles p
        WHERE p.id = auth.uid()
            AND p.role = 'LIBRARY_ADMIN'
    )
);

CREATE POLICY library_admin_delete
ON library.contents
FOR DELETE
USING (
    EXISTS (
        SELECT 1
        FROM identity.user_profiles p
        WHERE p.id = auth.uid()
          AND p.role = 'LIBRARY_ADMIN'
    )
);

-- Zabezpieczenie przed duplikatami --

CREATE OR REPLACE FUNCTION identity.handle_new_user()
RETURNS trigger
LANGUAGE plpgsql
SECURITY DEFINER
SET search_path = public, identity
AS $$
BEGIN
INSERT INTO identity.user_profiles(id)
VALUES (NEW.id)
    ON CONFLICT (id) DO NOTHING;

RETURN NEW;
END;
$$;

-- aktualizacja  i triggery --

CREATE OR REPLACE FUNCTION public.set_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
RETURN NEW;
END;
$$;

CREATE TRIGGER trg_user_profiles_updated
    BEFORE UPDATE ON identity.user_profiles
    FOR EACH ROW
    EXECUTE FUNCTION public.set_updated_at();

CREATE TRIGGER trg_contents_updated
    BEFORE UPDATE ON library.contents
    FOR EACH ROW
    EXECUTE FUNCTION public.set_updated_at();

CREATE TRIGGER trg_watchlists_updated
    BEFORE UPDATE ON recommendation.watchlists
    FOR EACH ROW
    EXECUTE FUNCTION public.set_updated_at();

CREATE TRIGGER trg_playbacks_updated
    BEFORE UPDATE ON playback.playbacks
    FOR EACH ROW
    EXECUTE FUNCTION public.set_updated_at();